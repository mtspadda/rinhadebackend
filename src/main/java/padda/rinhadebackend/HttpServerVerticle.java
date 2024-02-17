package padda.rinhadebackend;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;

public class HttpServerVerticle extends AbstractVerticle {

  private final PgPool client;

  public HttpServerVerticle(PgPool client) {
    this.client = client;
  }

  @Override
  public void start() {
    HttpServer server = vertx.createHttpServer();

    server.requestHandler(req -> {
      // Verifica se a requisição é uma POST e se contém o parâmetro 'valor'
      if ("POST".equals(req.method().name()) && req.getParam("valor") != null) {
        // Obtém o valor do parâmetro 'valor' da requisição
        double valor = Double.parseDouble(req.getParam("valor"));

        // Chama a função para descontar o valor do saldo
        descontarSaldo(valor, req.response());
      } else {
        // Retorna uma resposta indicando erro
        req.response()
          .setStatusCode(400)
          .putHeader("content-type", "application/json")
          .end(new JsonObject().put("mensagem", "Parâmetro 'valor' ausente na requisição.").encode());
      }
    });

    server.listen(8080, result -> {
      if (result.succeeded()) {
        System.out.println("Servidor Vert.x iniciado na porta 8080");
      } else {
        System.err.println("Erro ao iniciar o servidor Vert.x: " + result.cause());
      }
    });
  }

  private void descontarSaldo(double valor, io.vertx.core.http.HttpServerResponse response) {
    client.getConnection(ar -> {
      if (ar.succeeded()) {
        SqlConnection conn = ar.result();
        conn.preparedQuery("UPDATE contas SET saldo = saldo - $1 WHERE id = 1 RETURNING saldo")
          .execute(Tuple.of(valor), result -> {
            if (result.succeeded()) {
              Double novoSaldo = result.result().iterator().next().getDouble("saldo");
              System.out.println("Saldo restante: " + novoSaldo);
              response.putHeader("content-type", "application/json")
                .end(new JsonObject().put("saldo", novoSaldo).encode());
            } else {
              System.err.println("Erro ao descontar saldo: " + result.cause());
              response.setStatusCode(500).end();
            }
            conn.close();
          });
      } else {
        System.err.println("Erro ao obter conexão com o banco de dados: " + ar.cause());
        response.setStatusCode(500).end();
      }
    });
  }
}

