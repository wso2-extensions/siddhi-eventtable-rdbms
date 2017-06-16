/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.extension.siddhi.store.rdbms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.stream.input.InputHandler;

import java.sql.SQLException;

import static org.wso2.extension.siddhi.store.rdbms.RDBMSTableTestUtils.TABLE_NAME;
import static org.wso2.extension.siddhi.store.rdbms.RDBMSTableTestUtils.driverClassName;
import static org.wso2.extension.siddhi.store.rdbms.RDBMSTableTestUtils.url;

public class DeleteFromRDBMSTableTestCaseIT {
    private static final Log log = LogFactory.getLog(DeleteFromRDBMSTableTestCaseIT.class);

    @BeforeClass
    public static void startTest() {
        log.info("== RDBMS Table DELETE tests started ==");
    }

    @AfterClass
    public static void shutdown() {
        log.info("== RDBMS Table DELETE tests completed ==");
    }

    @BeforeMethod
    public void init() {
        try {
            RDBMSTableTestUtils.clearDatabaseTable(TABLE_NAME, RDBMSTableTestUtils.TestType.valueOf(System.getenv
                    ("DATABASE_TYPE")));
        } catch (SQLException e) {
            log.info("Test case ignored due to " + e.getMessage());
        }
    }

    @Test(description = "deleteFromRDBMSTableTest1")
    public void deleteFromRDBMSTableTest1() throws InterruptedException, SQLException {
        SiddhiManager siddhiManager = new SiddhiManager();
            String streams = "" +
                    "define stream StockStream (symbol string, price float, volume long); " +
                    "define stream DeleteStockStream (symbol string, price float, volume long); " +
                    "@Store(type=\"rdbms\", jdbc.url=\"" + url + "\", jdbc.driver.name=\"" + driverClassName + "\"," +
                    "username=\"root\", password=\"root\",field.length=\"symbol:100\")\n" +
                    "define table StockTable (symbol string, price float, volume long); ";
            String query = "" +
                    "@info(name = 'query1') " +
                    "from StockStream " +
                    "insert into StockTable ;" +
                    "" +
                    "@info(name = 'query2') " +
                    "from DeleteStockStream " +
                    "delete StockTable " +
                    "   on StockTable.symbol == symbol ;";

            ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);
            InputHandler stockStream = executionPlanRuntime.getInputHandler("StockStream");
            InputHandler deleteStockStream = executionPlanRuntime.getInputHandler("DeleteStockStream");
            executionPlanRuntime.start();

            stockStream.send(new Object[]{"WSO2", 55.6F, 100L});
            stockStream.send(new Object[]{"IBM", 75.6F, 100L});
            stockStream.send(new Object[]{"WSO2", 57.6F, 100L});
            deleteStockStream.send(new Object[]{"IBM", 57.6F, 100L});
            deleteStockStream.send(new Object[]{"WSO2", 57.6F, 100L});
            Thread.sleep(1000);

            long totalRowsInTable = RDBMSTableTestUtils.getRowsInTable(TABLE_NAME);
            Assert.assertEquals(totalRowsInTable, 0, "Deletion failed");
            executionPlanRuntime.shutdown();
    }


    @Test
    public void deleteFromRDBMSTableTest() throws InterruptedException, SQLException {
        log.info("deleteFromRDBMSTableTest2");
        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            RDBMSTableTestUtils.clearDatabaseTable(TABLE_NAME);
            String streams = "" +
                    "define stream StockStream (symbol string, price float, volume long); " +
                    "define stream DeleteStockStream (symbol string, price float, volume long); " +
                    "@Store(type=\"rdbms\", jdbc.url=\"" + url + "\", jdbc.driver.name=\"" + driverClassName + "\"," +
                    "username=\"root\", password=\"root\",field.length=\"symbol:100\")\n" +
                    //"@PrimaryKey(\"symbol\")" +
                    //"@Index(\"volume\")" +
                    "define table StockTable (symbol string, price float, volume long); ";
            String query = "" +
                    "@info(name = 'query1') " +
                    "from StockStream " +
                    "insert into StockTable ;" +
                    "" +
                    "@info(name = 'query2') " +
                    "from DeleteStockStream " +
                    "delete StockTable " +
                    "   on symbol == StockTable.symbol ;";

            ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);
            InputHandler stockStream = executionPlanRuntime.getInputHandler("StockStream");
            InputHandler deleteStockStream = executionPlanRuntime.getInputHandler("DeleteStockStream");
            executionPlanRuntime.start();

            stockStream.send(new Object[]{"WSO2", 55.6F, 100L});
            stockStream.send(new Object[]{"IBM", 75.6F, 100L});
            stockStream.send(new Object[]{"WSO2", 57.6F, 100L});
            deleteStockStream.send(new Object[]{"IBM", 57.6F, 100L});
            deleteStockStream.send(new Object[]{"WSO2", 57.6F, 100L});
            Thread.sleep(1000);

            long totalRowsInTable = RDBMSTableTestUtils.getRowsInTable(TABLE_NAME);
            Assert.assertEquals(totalRowsInTable, 0, "Deletion failed");
            executionPlanRuntime.shutdown();
        } catch (SQLException e) {
            log.info("Test case 'deleteFromRDBMSTableTest2' ignored due to " + e.getMessage());
            throw e;
        }
    }


    @Test
    public void deleteFromRDBMSTableTest3() throws InterruptedException, SQLException {
        log.info("deleteFromRDBMSTableTest3");
        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            RDBMSTableTestUtils.clearDatabaseTable(TABLE_NAME);
            String streams = "" +
                    "define stream StockStream (symbol string, price float, volume long); " +
                    "define stream DeleteStockStream (symbol string, price float, volume long); " +
                    "@Store(type=\"rdbms\", jdbc.url=\"" + url + "\", jdbc.driver.name=\"" + driverClassName + "\"," +
                    "username=\"root\", password=\"root\",field.length=\"symbol:100\")\n" +
                    //"@PrimaryKey(\"symbol\")" +
                    //"@Index(\"volume\")" +
                    "define table StockTable (symbol string, price float, volume long); ";
            String query = "" +
                    "@info(name = 'query1') " +
                    "from StockStream " +
                    "insert into StockTable ;" +
                    "" +
                    "@info(name = 'query2') " +
                    "from DeleteStockStream " +
                    "delete StockTable " +
                    "   on StockTable.symbol == 'IBM'  ;";

            ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);
            InputHandler stockStream = executionPlanRuntime.getInputHandler("StockStream");
            InputHandler deleteStockStream = executionPlanRuntime.getInputHandler("DeleteStockStream");
            executionPlanRuntime.start();

            stockStream.send(new Object[]{"WSO2", 55.6F, 100L});
            stockStream.send(new Object[]{"IBM", 75.6F, 100L});
            stockStream.send(new Object[]{"WSO2", 57.6F, 100L});
            deleteStockStream.send(new Object[]{"IBM", 57.6F, 100L});
            Thread.sleep(1000);

            long totalRowsInTable = RDBMSTableTestUtils.getRowsInTable(TABLE_NAME);
            Assert.assertEquals(totalRowsInTable, 2, "Deletion failed");
            executionPlanRuntime.shutdown();
        } catch (SQLException e) {
            log.info("Test case 'deleteFromRDBMSTableTest3' ignored due to " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void deleteFromRDBMSTableTest4() throws InterruptedException, SQLException {
        log.info("deleteFromRDBMSTableTest4");
        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            RDBMSTableTestUtils.clearDatabaseTable(TABLE_NAME);
            String streams = "" +
                    "define stream StockStream (symbol string, price float, volume long); " +
                    "define stream DeleteStockStream (symbol string, price float, volume long); " +
                    "@Store(type=\"rdbms\", jdbc.url=\"" + url + "\", jdbc.driver.name=\"" + driverClassName + "\"," +
                    "username=\"root\", password=\"root\",field.length=\"symbol:100\")\n" +
                    //"@PrimaryKey(\"symbol\")" +
                    //"@Index(\"volume\")" +
                    "define table StockTable (symbol string, price float, volume long); ";
            String query = "" +
                    "@info(name = 'query1') " +
                    "from StockStream " +
                    "insert into StockTable ;" +
                    "" +
                    "@info(name = 'query2') " +
                    "from DeleteStockStream " +
                    "delete StockTable " +
                    "   on 'IBM' == StockTable.symbol  ;";

            ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);
            InputHandler stockStream = executionPlanRuntime.getInputHandler("StockStream");
            InputHandler deleteStockStream = executionPlanRuntime.getInputHandler("DeleteStockStream");
            executionPlanRuntime.start();

            stockStream.send(new Object[]{"WSO2", 55.6F, 100L});
            stockStream.send(new Object[]{"IBM", 75.6F, 100L});
            stockStream.send(new Object[]{"WSO2", 57.6F, 100L});
            deleteStockStream.send(new Object[]{"IBM", 57.6F, 100L});
            Thread.sleep(1000);

            long totalRowsInTable = RDBMSTableTestUtils.getRowsInTable(TABLE_NAME);
            Assert.assertEquals(totalRowsInTable, 2, "Deletion failed");
            executionPlanRuntime.shutdown();
        } catch (SQLException e) {
            log.info("Test case 'deleteFromRDBMSTableTest4' ignored due to " + e.getMessage());
            throw e;
        }
    }

    @Test(enabled = false)
    public void deleteFromRDBMSTableTest5() throws InterruptedException, SQLException {
        // TODO VERIFY CORRECTNESS
        log.info("deleteFromRDBMSTableTest5");
        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            RDBMSTableTestUtils.clearDatabaseTable(TABLE_NAME);
            String streams = "" +
                    "define stream StockStream (symbol string, price float, volume long); " +
                    "define stream DeleteStockStream (symbol string, price float, volume long); " +
                    "@Store(type=\"rdbms\", jdbc.url=\"" + url + "\", jdbc.driver.name=\"" + driverClassName + "\"," +
                    "username=\"root\", password=\"root\",field.length=\"symbol:100\")\n" +
                    //"@PrimaryKey(\"symbol\")" +
                    //"@Index(\"volume\")" +
                    "define table StockTable (symbol string, price float, volume long); ";
            String query = "" +
                    "@info(name = 'query1') " +
                    "from StockStream " +
                    "insert into StockTable ;" +
                    "" +
                    "@info(name = 'query2') " +
                    "from DeleteStockStream " +
                    "delete StockTable " +
                    "   on 'IBM' == symbol  ;";

            ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);
            InputHandler stockStream = executionPlanRuntime.getInputHandler("StockStream");
            InputHandler deleteStockStream = executionPlanRuntime.getInputHandler("DeleteStockStream");
            executionPlanRuntime.start();

            stockStream.send(new Object[]{"WSO2", 55.6F, 100L});
            stockStream.send(new Object[]{"IBM", 75.6F, 100L});
            stockStream.send(new Object[]{"WSO2", 57.6F, 100L});
            deleteStockStream.send(new Object[]{"WSO2", 57.6F, 100L});
            Thread.sleep(1000);

            long totalRowsInTable = RDBMSTableTestUtils.getRowsInTable(TABLE_NAME);
            Assert.assertEquals(totalRowsInTable, 2, "Deletion failed");
            executionPlanRuntime.shutdown();
        } catch (SQLException e) {
            log.info("Test case 'deleteFromRDBMSTableTest5' ignored due to " + e.getMessage());
            throw e;
        }

    }

    @Test(enabled = false)
    public void deleteFromRDBMSTableTest6() throws InterruptedException, SQLException {
        // TODO VERIFY CORRECTNESS
        log.info("deleteFromRDBMSTableTest6");
        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            RDBMSTableTestUtils.clearDatabaseTable(TABLE_NAME);
            String streams = "" +
                    "define stream StockStream (symbol string, price float, volume long); " +
                    "define stream DeleteStockStream (symbol string, price float, volume long); " +
                    "@Store(type=\"rdbms\", jdbc.url=\"" + url + "\", jdbc.driver.name=\"" + driverClassName + "\"," +
                    "username=\"root\", password=\"root\",field.length=\"symbol:100\")\n" +
                    //"@PrimaryKey(\"symbol\")" +
                    //"@Index(\"volume\")" +
                    "define table StockTable (symbol string, price float, volume long); ";
            String query = "" +
                    "@info(name = 'query1') " +
                    "from StockStream " +
                    "insert into StockTable ;" +
                    "" +
                    "@info(name = 'query2') " +
                    "from DeleteStockStream " +
                    "delete StockTable " +
                    "   on symbol == 'IBM'  ;";

            ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);
            InputHandler stockStream = executionPlanRuntime.getInputHandler("StockStream");
            InputHandler deleteStockStream = executionPlanRuntime.getInputHandler("DeleteStockStream");
            executionPlanRuntime.start();

            stockStream.send(new Object[]{"WSO2", 55.6F, 100L});
            stockStream.send(new Object[]{"IBM", 75.6F, 100L});
            stockStream.send(new Object[]{"WSO2", 57.6F, 100L});
            deleteStockStream.send(new Object[]{"IBM", 57.6F, 100L});
            Thread.sleep(1000);

            long totalRowsInTable = RDBMSTableTestUtils.getRowsInTable(TABLE_NAME);
            Assert.assertEquals(totalRowsInTable, 2, "Deletion failed");
            executionPlanRuntime.shutdown();
        } catch (SQLException e) {
            log.info("Test case 'deleteFromRDBMSTableTest6' ignored due to " + e.getMessage());
            throw e;
        }
    }


    @Test
    public void deleteFromRDBMSTableTest7() throws InterruptedException, SQLException {
        log.info("deleteFromRDBMSTableTest7");
        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            RDBMSTableTestUtils.clearDatabaseTable(TABLE_NAME);
            String streams = "" +
                    "define stream StockStream (symbol string, price float, volume long); " +
                    "define stream DeleteStockStream (symbol string, price float, volume long); " +
                    "@Store(type=\"rdbms\", jdbc.url=\"" + url + "\", jdbc.driver.name=\"" + driverClassName + "\"," +
                    "username=\"root\", password=\"root\",field.length=\"symbol:100\")\n" +
                    //"@PrimaryKey(\"symbol\")" +
                    //"@Index(\"volume\")" +
                    "define table StockTable (symbol string, price float, volume long); ";
            String query = "" +
                    "@info(name = 'query1') " +
                    "from StockStream " +
                    "insert into StockTable ;" +
                    "" +
                    "@info(name = 'query2') " +
                    "from DeleteStockStream " +
                    "delete StockTable " +
                    "   on StockTable.symbol==symbol and StockTable.price > price and  StockTable.volume == volume  ;";

            ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);
            InputHandler stockStream = executionPlanRuntime.getInputHandler("StockStream");
            InputHandler deleteStockStream = executionPlanRuntime.getInputHandler("DeleteStockStream");
            executionPlanRuntime.start();

            stockStream.send(new Object[]{"WSO2", 55.6F, 100L});
            stockStream.send(new Object[]{"IBM", 75.6F, 100L});
            stockStream.send(new Object[]{"IBM", 57.6F, 100L});
            deleteStockStream.send(new Object[]{"IBM", 57.6F, 100L});
            Thread.sleep(1000);

            long totalRowsInTable = RDBMSTableTestUtils.getRowsInTable(TABLE_NAME);
            Assert.assertEquals(totalRowsInTable, 2);
            executionPlanRuntime.shutdown();
        } catch (SQLException e) {
            log.info("Test case 'deleteFromRDBMSTableTest7' ignored due to " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void deleteFromRDBMSTableTest8() throws InterruptedException, SQLException {
        log.info("deleteFromRDBMSTableTest8");
        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            RDBMSTableTestUtils.clearDatabaseTable(TABLE_NAME);
            String streams = "" +
                    "define stream StockStream (symbol string, price float, volume long); " +
                    "define stream DeleteStockStream (symbol string, price float, volume long); " +
                    "@Store(type=\"rdbms\", jdbc.url=\"" + url + "\", jdbc.driver.name=\"" + driverClassName + "\"," +
                    "username=\"root\", password=\"root\",field.length=\"symbol:100\")\n" +
                    //"@PrimaryKey(\"symbol\")" +
                    //"@Index(\"volume\")" +
                    "define table StockTable (symbol string, price float, volume long); ";
            String query = "" +
                    "@info(name = 'query1') " +
                    "from StockStream " +
                    "insert into StockTable ;" +
                    "" +
                    "@info(name = 'query2') " +
                    "from DeleteStockStream " +
                    "delete StockTable " +
                    "   on StockTable.symbol=='IBM' and StockTable.price > 50 and  StockTable.volume == volume  ;";

            ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);
            InputHandler stockStream = executionPlanRuntime.getInputHandler("StockStream");
            InputHandler deleteStockStream = executionPlanRuntime.getInputHandler("DeleteStockStream");
            executionPlanRuntime.start();

            stockStream.send(new Object[]{"WSO2", 55.6F, 100L});
            stockStream.send(new Object[]{"IBM", 75.6F, 100L});
            stockStream.send(new Object[]{"IBM", 57.6F, 100L});
            deleteStockStream.send(new Object[]{"IBM", 57.6F, 100L});
            Thread.sleep(1000);

            long totalRowsInTable = RDBMSTableTestUtils.getRowsInTable(TABLE_NAME);
            Assert.assertEquals(totalRowsInTable, 1, "Deletion failed");
            executionPlanRuntime.shutdown();
        } catch (SQLException e) {
            log.info("Test case 'deleteFromRDBMSTableTest8' ignored due to " + e.getMessage());
            throw e;
        }
    }


    @Test
    public void deleteFromRDBMSTableTest10() throws InterruptedException, SQLException {
        log.info("deleteFromRDBMSTableTest10");
        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            RDBMSTableTestUtils.clearDatabaseTable(TABLE_NAME);
            String streams = "" +
                    "define stream StockStream (symbol string, price float, volume long); " +
                    "define stream DeleteStockStream (symbol string, price float, volume long); " +
                    "@Store(type=\"rdbms\", jdbc.url=\"" + url + "\", jdbc.driver.name=\"" + driverClassName + "\"," +
                    "username=\"root\", password=\"root\",field.length=\"symbol:100\")\n" +
                    //"@PrimaryKey(\"symbol\")" +
                    //"@Index(\"volume\")" +
                    "define table StockTable (symbol string, price float, volume long); ";
            String query = "" +
                    "@info(name = 'query1') " +
                    "from StockStream " +
                    "insert into StockTable ;" +
                    "" +
                    "@info(name = 'query2') " +
                    "from DeleteStockStream " +
                    "delete StockTable " +
                    "   on StockTable.symbol == symbol ;";

            ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);
            InputHandler stockStream = executionPlanRuntime.getInputHandler("StockStream");
            InputHandler deleteStockStream = executionPlanRuntime.getInputHandler("DeleteStockStream");
            executionPlanRuntime.start();

            stockStream.send(new Object[]{"WSO2", 55.6F, 100L});
            stockStream.send(new Object[]{"IBM", 75.6F, 100L});
            stockStream.send(new Object[]{"WSO2", 57.6F, 100L});
            deleteStockStream.send(new Object[]{"IBM", 57.6F, 100L});
            Thread.sleep(1000);

            long totalRowsInTable = RDBMSTableTestUtils.getRowsInTable(TABLE_NAME);
            Assert.assertEquals(totalRowsInTable, 2, "Deletion failed");
            executionPlanRuntime.shutdown();
        } catch (SQLException e) {
            log.info("Test case 'deleteFromRDBMSTableTest10' ignored due to " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void deleteFromRDBMSTableTest11() throws InterruptedException, SQLException {
        log.info("deleteFromRDBMSTableTest11");
        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            RDBMSTableTestUtils.clearDatabaseTable(TABLE_NAME);
            String streams = "" +
                    "define stream StockStream (symbol string, price float, volume long); " +
                    "define stream DeleteStockStream (symbol string, price float, volume long); " +
                    "@Store(type=\"rdbms\", jdbc.url=\"" + url + "\", jdbc.driver.name=\"" + driverClassName + "\"," +
                    "username=\"root\", password=\"root\",field.length=\"symbol:100\")\n" +
                    //"@PrimaryKey(\"symbol\")" +
                    //"@Index(\"volume\")" +
                    "define table StockTable (symbol string, price float, volume long); ";
            String query = "" +
                    "@info(name = 'query1') " +
                    "from StockStream " +
                    "insert into StockTable ;" +
                    "" +
                    "@info(name = 'query2') " +
                    "from DeleteStockStream " +
                    "delete StockTable " +
                    "   on StockTable.symbol == symbol ;";

            ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);
            InputHandler stockStream = executionPlanRuntime.getInputHandler("StockStream");
            InputHandler deleteStockStream = executionPlanRuntime.getInputHandler("DeleteStockStream");
            executionPlanRuntime.start();

            stockStream.send(new Object[]{"WSO2", 55.6F, 100L});
            stockStream.send(new Object[]{"IBM", 75.6F, 100L});
            stockStream.send(new Object[]{"WSO2", 57.6F, 100L});
            deleteStockStream.send(new Object[]{"IBM", 57.6F, 100L});
            Thread.sleep(1000);

            long totalRowsInTable = RDBMSTableTestUtils.getRowsInTable(TABLE_NAME);
            Assert.assertEquals(totalRowsInTable, 2, "Deletion failed");
            Thread.sleep(1000);

            stockStream.send(new Object[]{null, 45.5F, 100L});
            executionPlanRuntime.shutdown();
            Thread.sleep(1000);
            try {
                siddhiManager.createExecutionPlanRuntime(streams + query);
            } catch (NullPointerException ex) {
                Assert.fail("Cannot Process null values in bloom filter");
            }
        } catch (SQLException e) {
            log.info("Test case 'deleteFromRDBMSTableTest11' ignored due to " + e.getMessage());
            throw e;
        }
    }
}