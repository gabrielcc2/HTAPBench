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
package pt.haslab.htapbench.core;

import pt.haslab.htapbench.api.Worker;
import pt.haslab.htapbench.densitity.Clock;
import pt.haslab.htapbench.util.QueueLimitException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OLTPWorkerThread implements Runnable{
    
    List<Worker> workers;
    List<WorkloadConfiguration> workConfs;
    int intervalMonitor;
    boolean calibrate;
    Results results;
    private Clock clock;

    public OLTPWorkerThread(List<Worker> workers, List<WorkloadConfiguration> workConfs, int intervalMonitoring, boolean calibrate){
        this.workers=workers;
        this.workConfs=workConfs;
        this.intervalMonitor=intervalMonitoring;
        this.calibrate=calibrate;
    }
    
    @Override
    public void run() {
        try {
            results = ThreadBench.runRateLimitedOLTP(workers, workConfs, intervalMonitor, calibrate);
        } catch (QueueLimitException ex) {
            Logger.getLogger(OLTPWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OLTPWorkerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Results getResults(){
        return results;
    }
    
}
