package com.chu;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;

public class CreateHBaseTableDemo
{

    public static Configuration configuration;

    static
    {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("hbase.zookeeper.quorum", "192.168.1.159");
        configuration.set("hbase.master", "192.168.1.159:600000");
    }

    public static void main(String[] args)
    {
        //createTable("HBaseDemoTable1");
        //insertData("HBaseDemoTable1");
        //QueryAll("HBaseDemoTable1");
        //QueryByCondition1("HBaseDemoTable1");
        //QueryByCondition2("HBaseDemoTable1");
        QueryByCondition3("HBaseDemoTable1");
        //deleteRow("wujintao","abcdef");
        //deleteByCondition("wujintao", "abcdef");
    }

    /**
     * 创建表
     *
     * @param tableName
     */
    public static void createTable(String tableName)
    {
        System.out.println("start create table ......");
        try
        {
            HBaseAdmin hBaseAdmin = new HBaseAdmin(configuration);
            if (hBaseAdmin.tableExists(tableName))
            {
                // 如果存在要创建的表，那么先删除，再创建
                hBaseAdmin.disableTable(tableName);
                hBaseAdmin.deleteTable(tableName);
                System.out.println(tableName + " is exist,detele....");
            }
            HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
            tableDescriptor.addFamily(new HColumnDescriptor("column1"));
            tableDescriptor.addFamily(new HColumnDescriptor("column2"));
            tableDescriptor.addFamily(new HColumnDescriptor("column3"));
            hBaseAdmin.createTable(tableDescriptor);
        } catch (MasterNotRunningException e)
        {
            e.printStackTrace();
        } catch (ZooKeeperConnectionException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("end create table ......");
    }

    /**
     * 插入数据
     *
     * @param tableName
     */
    public static void insertData(String tableName)  {

        System.out.println("start insert data ......");

        try
        {


            HTable table = new HTable(configuration, tableName);

            Put put = new Put(Bytes.toBytes("45678hyhyjju"));
            put.add(Bytes.toBytes("column1"),
                    Bytes.toBytes("email"),
                    Bytes.toBytes("5211486@qq.com"));
            table.put(put);// 放入表
            table.close();// 释放资源
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("end insert data ......");


    }

    /**
     * 删除一张表
     *
     * @param tableName
     */
    public static void dropTable(String tableName) {
        try {
            HBaseAdmin admin = new HBaseAdmin(configuration);
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
        } catch (MasterNotRunningException e) {
            e.printStackTrace();
        } catch (ZooKeeperConnectionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据 rowkey删除一条记录
     *
     * @param tablename
     * @param rowkey
     */
    public static void deleteRow(String tablename, String rowkey) {
        try {
            HTable table = new HTable(configuration, tablename);
            List list = new ArrayList();
            Delete d1 = new Delete(rowkey.getBytes());
            list.add(d1);

            table.delete(list);
            System.out.println("删除行成功!");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 组合条件删除
     *
     * @param tablename
     * @param rowkey
     */
    public static void deleteByCondition(String tablename, String rowkey) {
        //目前还没有发现有效的API能够实现 根据非rowkey的条件删除 这个功能能，还有清空表全部数据的API操作

    }


    /**
     * 查询所有数据
     *
     * @param tableName
     */
    public static void QueryAll(String tableName) {


        try
        {
            HTable table = new HTable(configuration, tableName);
            ResultScanner rs = table.getScanner(new Scan());
            for (Result r : rs)
            {
                System.out.println("获得到rowkey:" + new String(r.getRow()));
                for (KeyValue keyValue : r.raw())
                {
                    System.out.println("列：" + new String(keyValue.getFamily())
                            + "====值:" + new String(keyValue.getValue()));
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 单条件查询,根据rowkey查询唯一一条记录
     *
     * @param tableName
     */
    public static void QueryByCondition1(String tableName) {


        try {
            HTable table = new HTable(configuration, tableName);
            Get scan = new Get("45678hyhyjju".getBytes());// 根据rowkey查询
            Result r = table.get(scan);
            System.out.println("获得到rowkey:" + new String(r.getRow()));
            for (KeyValue keyValue : r.raw()) {
                System.out.println("列：" + new String(keyValue.getFamily())
                        + "====值:" + new String(keyValue.getValue()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 单条件按查询，查询多条记录
     *
     * @param tableName
     */
    public static void QueryByCondition2(String tableName) {

        try {
            HTable table = new HTable(configuration, tableName);
            Filter filter = new SingleColumnValueFilter(Bytes
                    .toBytes("column1"), null, CompareOp.EQUAL, Bytes
                    .toBytes("123456")); // 当列column1的值为aaa时进行查询
            Scan s = new Scan();
            s.setFilter(filter);
            ResultScanner rs = table.getScanner(s);
            for (Result r : rs) {
                System.out.println("获得到rowkey:" + new String(r.getRow()));
                for (KeyValue keyValue : r.raw()) {
                    System.out.println("列：" + new String(keyValue.getFamily())
                            + "====值:" + new String(keyValue.getValue()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 组合条件查询
     *
     * @param tableName
     */
    public static void QueryByCondition3(String tableName) {

        try {
            HTable table = new HTable(configuration, tableName);

            List<Filter> filters = new ArrayList<Filter>();

            Filter filter1 = new SingleColumnValueFilter(Bytes
                    .toBytes("column1"), null, CompareOp.EQUAL, Bytes
                    .toBytes("123456"));
            filters.add(filter1);

            Filter filter2 = new SingleColumnValueFilter(Bytes
                    .toBytes("column2"), null, CompareOp.EQUAL, Bytes
                    .toBytes("123456"));
            filters.add(filter2);

            Filter filter3 = new SingleColumnValueFilter(Bytes
                    .toBytes("column3"), null, CompareOp.EQUAL, Bytes
                    .toBytes("c2g111"));
            filters.add(filter3);

            FilterList filterList1 = new FilterList(filters);

            Scan scan = new Scan();
            scan.setFilter(filterList1);
            ResultScanner rs = table.getScanner(scan);
            for (Result r : rs) {
                System.out.println("获得到rowkey:" + new String(r.getRow()));
                for (KeyValue keyValue : r.raw()) {
                    System.out.println("列：" + new String(keyValue.getFamily())
                            + "====值:" + new String(keyValue.getValue()));
                }
            }
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
