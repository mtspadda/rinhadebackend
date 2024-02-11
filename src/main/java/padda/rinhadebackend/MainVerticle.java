package padda.rinhadebackend;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;

public class MainVerticle extends AbstractVerticle {

  private SqlClient client;

  @Override
  public void start() {
    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(5432)
      .setHost("db")
      .setDatabase("rinhadb")
      .setUser("admin")
      .setPassword("admin");
    PoolOptions poolOptions = new PoolOptions().setMaxSize(4);
    this.client = PgPool.client(vertx, connectOptions, poolOptions);
  }

  private void criaPessoa(RoutingContext routingContext){
    String limit, saldo_inicial;
    String[] stack;
  }


}
