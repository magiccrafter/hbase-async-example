## Overview
Asynchronous HBase Client POC for reading data from HBase Standalone Server and HBase instance part of Cloudera cluster.  

version used:  
```
 <dependency>
    <groupId>org.hbase</groupId>
    <artifactId>asynchbase</artifactId>
    <version>1.8.0</version>
  </dependency>
```

##Prerequisites
All command are executed in the HBase shell for convenience.

- Create a table with the following definition:   
`hbase> create 'nvtest', 'f1'` 
- Insert some data in the table:   
`hbase> put 'nvtest', 'row1', 'f1:type', '15'`
- Verify data inserted correctly:  
`hbase> get 'nvtest', 'row1', 'f1:type'`  
expected output: 
  ```
  hbase(main):010:0> get 'nvtest', 'row1', 'f1:type'
  COLUMN                                                 CELL
   f1:type                                               timestamp=1506671133214, value=15
  1 row(s) in 0.0140 seconds
  ```
  
##Run
Replace the placeholders with the proper values and uncomment/comment the right method in main.