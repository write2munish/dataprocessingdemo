Data Processing Demo

This Proof of Concept is meant to demonstrate the power of Actor model when processing large amount of data, coupled with JDBC batch update mode.



Installation

1. Download and install MySQL
2. Create Database with name mockDB
3. Run the scripts in the folder data/MOCK_DATA.sql to create the table and populate the data with 100K rows
4. Incase of any changes in database names or location, please update the corresponding actor files - LoadDataActor and UpdateDBActor

Execution

1. You can run the program org.demo.data.processing.DBTest to get the time required to process each record 
