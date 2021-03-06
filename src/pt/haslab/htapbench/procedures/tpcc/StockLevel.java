/*
 * Copyright 2017 by INESC TEC                                               
 * Developed by Fábio Coelho                                                 
 * This work was based on the OLTPBenchmark Project                          
 *
 * Licensed under the Apache License, Version 2.0 (the "License");           
 * you may not use this file except in compliance with the License.          
 * You may obtain a copy of the License at                                   
 *
 * http://www.apache.org/licenses/LICENSE-2.0                              
 *
 * Unless required by applicable law or agreed to in writing, software       
 * distributed under the License is distributed on an "AS IS" BASIS,         
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  
 * See the License for the specific language governing permissions and       
 * limitations under the License.                                            
 */
package pt.haslab.htapbench.procedures.tpcc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import org.apache.log4j.Logger;
import pt.haslab.htapbench.api.SQLStmt;
import pt.haslab.htapbench.benchmark.HTAPBConstants;
import pt.haslab.htapbench.benchmark.TPCCUtil;
import pt.haslab.htapbench.benchmark.TPCCWorker;
import pt.haslab.htapbench.benchmark.jTPCCConfig;

public class StockLevel extends TPCCProcedure {

    private static final Logger LOG = Logger.getLogger(StockLevel.class);
    //keying time in seconds.
    private final long keyingTime = jTPCCConfig.keyingTime_StockLevel;

	public SQLStmt stockGetDistOrderIdSQL = new SQLStmt("SELECT D_NEXT_O_ID FROM DISTRICT WHERE D_W_ID = ? AND D_ID = ?");

	public SQLStmt stockGetCountStockSQL = new SQLStmt("SELECT COUNT(DISTINCT (S_I_ID)) AS STOCK_COUNT"
			+ " FROM " + HTAPBConstants.TABLENAME_ORDERLINE + ", " + HTAPBConstants.TABLENAME_STOCK
			+ " WHERE OL_W_ID = ?"
			+ " AND OL_D_ID = ?"
			+ " AND OL_O_ID < ?"
			+ " AND OL_O_ID >= ? - 20"
			+ " AND S_W_ID = ?"
			+ " AND S_I_ID = OL_I_ID" + " AND S_QUANTITY < ?");

	// Stock Level Txn
        
	private PreparedStatement stockGetDistOrderId = null;
	private PreparedStatement stockGetCountStock = null;

    @Override
	 public ResultSet run(Connection conn, Random gen,
				int terminalWarehouseID, int numWarehouses,
				int terminalDistrictLowerID, int terminalDistrictUpperID,
				TPCCWorker w) throws SQLException {
                                
		stockGetDistOrderId = this.getPreparedStatement(conn, stockGetDistOrderIdSQL);
		stockGetCountStock= this.getPreparedStatement(conn, stockGetCountStockSQL);
		int threshold = TPCCUtil.randomNumber(10, 20, gen);

		int districtID = TPCCUtil.randomNumber(terminalDistrictLowerID,terminalDistrictUpperID, gen);

		stockLevelTransaction(terminalWarehouseID, districtID, threshold,conn,w);

		return null;
	 }



		private void stockLevelTransaction(int w_id, int d_id, int threshold, Connection conn,TPCCWorker w)
				throws SQLException {
			int o_id = 0;
			// XXX int i_id = 0;
			int stock_count = 0;

                        PreparedStatement stockGetDistOrderId = getPreparedStatement(conn, stockGetDistOrderIdSQL);
			stockGetDistOrderId.setInt(1, w_id);
			stockGetDistOrderId.setInt(2, d_id);
			ResultSet rs = stockGetDistOrderId.executeQuery();

			if (!rs.next())
				throw new RuntimeException("D_W_ID="+ w_id +" D_ID="+ d_id+" not found!");
			o_id = rs.getInt("D_NEXT_O_ID");
			rs.close();
			rs = null;
                        PreparedStatement stockGetCountStock = getPreparedStatement(conn, stockGetCountStockSQL);

			stockGetCountStock.setInt(1, w_id);
			stockGetCountStock.setInt(2, d_id);
			stockGetCountStock.setInt(3, o_id);
			stockGetCountStock.setInt(4, o_id);
			stockGetCountStock.setInt(5, w_id);
			stockGetCountStock.setInt(6, threshold);
			rs = stockGetCountStock.executeQuery();

			if (!rs.next())
				throw new RuntimeException("OL_W_ID="+w_id +" OL_D_ID="+d_id+" OL_O_ID="+o_id+" not found!");
			stock_count = rs.getInt("STOCK_COUNT");

			conn.commit();

			rs.close();
			rs = null;

			StringBuilder terminalMessage = new StringBuilder();
			terminalMessage
					.append("\n+-------------------------- STOCK-LEVEL --------------------------+");
			terminalMessage.append("\n Warehouse: ");
			terminalMessage.append(w_id);
			terminalMessage.append("\n District:  ");
			terminalMessage.append(d_id);
			terminalMessage.append("\n\n Stock Level Threshold: ");
			terminalMessage.append(threshold);
			terminalMessage.append("\n Low Stock Count:       ");
			terminalMessage.append(stock_count);
			terminalMessage
					.append("\n+-----------------------------------------------------------------+\n\n");
			if(LOG.isTraceEnabled())LOG.trace(terminalMessage.toString());
		}
    public long getKeyingTime(){
        return this.keyingTime;
    }            
}
