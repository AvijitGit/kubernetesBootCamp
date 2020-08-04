package com.tcg.mrl.batch.process.mrlreportbatchprocess.batchprocessing;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class RangePartitioner implements Partitioner {

    private final DataSource dataSource;

    public RangePartitioner(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        JdbcOperations jdbcTemplate = new JdbcTemplate(dataSource);
        Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();
        System.out.println("grid size----- " + gridSize);
        int records = jdbcTemplate.queryForObject("SELECT count(*) FROM mrldb.trn_trial_bal_base_1" , Integer.class);
        System.out.println("records" + records);
        int range = records/gridSize;
        int reminder = records%gridSize;
        int fromId = 1;
        int toId = range;
        for (int i = 1; i <= gridSize; i++) {
            ExecutionContext value = new ExecutionContext();
            value.putInt("fromId", fromId);
            if(i==gridSize){
                value.putInt("toId", Integer.sum(toId,reminder));
            }else{
                value.putInt("toId", toId);
            }
            // give each thread a name, thread 1,2,3
            value.putString("name", "Thread" + i);
            result.put("partition" + i, value);
            fromId = toId + 1;
            toId+= range;
        }
        return result;
    }
}
