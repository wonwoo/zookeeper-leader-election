package me.wonwoo;

import me.wonwoo.election.LeaderSelector;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Created by wonwoolee on 2017. 8. 30..
 */
public class Main1 {

  public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

    new LeaderSelector("127.0.0.1", 3000, "main1");
    TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
  }
}
