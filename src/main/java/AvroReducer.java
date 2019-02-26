import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapred.AvroValue;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @Author: xu.dm
 * @Date: 2019/2/25 17:17
 * @Description:
 */
public class AvroReducer extends Reducer<AvroKey<Integer>,AvroValue<GenericRecord>,AvroKey<GenericRecord>,NullWritable> {
    /**
     * This method is called once for each key. Most applications will define
     * their reduce class by overriding this method. The default implementation
     * is an identity function.
     *
     * @param key
     * @param values
     * @param context
     */
    @Override
    protected void reduce(AvroKey<Integer> key, Iterable<AvroValue<GenericRecord>> values, Context context) throws IOException, InterruptedException {
        GenericRecord max = null;
        for (AvroValue<GenericRecord> value : values){
            GenericRecord record = value.datum();
            if(max==null ||
                    (Integer)record.get("temperature") > (Integer) max.get("temperature")){
                //必须重新生成GenericRecord，不能直接max=record进行对象引用
                //迭代算法为了高效，重用了record
                max = newRecord(record);
            }
        }
        context.write(new AvroKey<>(max),NullWritable.get());
    }

    private GenericRecord newRecord(GenericRecord value){
        GenericRecord record = new GenericData.Record(AvroSchemas.SCHEMA);
        record.put("year",value.get("year"));
        record.put("temperature",value.get("temperature"));

        return record;
    }
}
