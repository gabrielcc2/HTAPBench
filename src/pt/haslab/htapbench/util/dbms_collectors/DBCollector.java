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
package pt.haslab.htapbench.util.dbms_collectors;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.TreeMap;

class DBCollector implements DBParameterCollector {
    private static final Logger LOG = Logger.getLogger(DBCollector.class);
    protected final Map<String, String> dbConf = new TreeMap<String, String>();

    public String collectParameters() {
        StringBuilder confBuilder = new StringBuilder();
        for (Map.Entry<String, String> kv : dbConf.entrySet()) {
            confBuilder.append(kv.getKey().toLowerCase())
                       .append("=")
                       .append(kv.getValue().toLowerCase())
                       .append("\n");
        }
        return confBuilder.toString();
    }

    public String collectVersion() {
        return "";
    }
}
