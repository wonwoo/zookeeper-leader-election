package me.wonwoo.election;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * Created by wonwoolee on 2017. 8. 31..
 */
public class LeaderSelector implements Watcher {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  private String path = "/election";
  private String name = UUID.randomUUID().toString();
  private final ZooKeeper zooKeeper;


  public LeaderSelector(String host, int sessionTimeout, String name) {
    this.name = name;
    try {
      this.zooKeeper = new ZooKeeper(host, sessionTimeout, this);
      Stat stat = zooKeeper.exists(path, false);
      if (stat == null) {
        String r = zooKeeper.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,
            CreateMode.PERSISTENT);
        logger.info("{}  created", r);
      }
      String childPath = path + "/n_";
      String s = zooKeeper.create(childPath, this.name.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
          CreateMode.EPHEMERAL_SEQUENTIAL);
      System.out.println(s + " created");
      leaderElection();

    } catch (Exception e) {
      throw new RuntimeException("fail zookeeper", e);
    }
  }

  public LeaderSelector(String host, int sessionTimeout) {
    this(host, sessionTimeout, "");
  }

  private void leaderElection() {
    try {
      List<String> children = zooKeeper.getChildren(path, false);

      String tmp = children.get(0);
      // j < i and n_j
      for (String s : children) {
        if (tmp.compareTo(s) > 0)
          tmp = s;
      }
      String leader = path + "/" + tmp;
      Stat stat = zooKeeper.exists(leader, true);
      zooKeeper.getData(leader, true, new AsyncCallback.DataCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
          System.out.println("leader Name : " + new String(data));
          System.out.println("leader Path : " + path);
          System.out.println("leader Id : " + stat.getEphemeralOwner());
        }
      }, stat);

    } catch (Exception e) {
      throw new RuntimeException("fail zookeeper", e);
    }

  }

  @Override
  public void process(WatchedEvent event) {
    switch (event.getType()) {
      case NodeDeleted:
        leaderElection();
        break;
      case None:
        switch (event.getState()) {
          case Disconnected:
            System.out.println("Disconnected.");
            break;
          case Expired:
            System.out.println("Expired.");
            break;
          case SyncConnected:
            System.out.println("SyncConnected.");
            break;
        }
    }
  }

  public void setPath(String path) {
    this.path = path;
  }
}
