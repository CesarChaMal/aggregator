# Financial Instrument Price Aggregator

A high-performance Java application for processing and aggregating financial instrument price data streams using reactive programming principles with RxJava.

## Overview

This application processes financial instrument price data from input files and performs various statistical calculations including mean, maximum, and sum aggregations. It features a streaming architecture designed to handle large datasets efficiently while maintaining low memory consumption.

## Key Features

- **Reactive Streaming**: Built with RxJava for efficient stream processing with backpressure control
- **Database Integration**: H2 in-memory database for instrument price multipliers with caching
- **Error Handling**: Robust error handling with failed parsing stream reporting
- **Scalable Architecture**: Designed to handle gigabytes of data with minimal memory footprint
- **Business Date Validation**: Filters out non-business days (weekends)
- **Configurable Calculations**: Extensible calculation engine for various statistical operations

## Architecture

### Core Components

- **StreamDriver**: Main orchestrator that coordinates the data processing pipeline
- **Aggregator**: Manages the input stream and parsing operations
- **InstrumentPriceParser**: Parses CSV input lines into InstrumentPrice objects
- **MultiplierProvider**: Handles database lookups for price multipliers with 5-second caching
- **InstrumentPriceUtilities**: Provides utility functions for filtering and calculations

### Data Flow

1. Input data is read from CSV files
2. Lines are parsed into InstrumentPrice objects
3. Business date validation is applied
4. Price multipliers are applied from database
5. Statistical calculations are performed
6. Results are output to console and files

## Calculations Performed

- **INSTRUMENT1**: Mean of all prices
- **INSTRUMENT2**: Mean of prices for November 2014 only
- **INSTRUMENT3**: Maximum price value
- **Other Instruments**: Sum of the 10 most recent prices (by date)

## Database Schema

The application uses an H2 in-memory database with the following table:

```sql
CREATE TABLE INSTRUMENT_PRICE_MODIFIER (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR2(256) NOT NULL UNIQUE,
    multiplier NUMBER(10,2) NOT NULL
);
```

Sample data includes multipliers for INSTRUMENT1 (1.05), INSTRUMENT2 (1.10), INSTRUMENT3 (1.15), and INSTRUMENT5 (2.0).

## Input Data Format

The application expects CSV files with the following format:
```
<INSTRUMENT_NAME>,<DATE>,<VALUE>
```

Example:
```
INSTRUMENT1,12-Mar-2015,12.21
INSTRUMENT2,13-Mar-2015,15.30
```

Date format: `dd-MMM-yyyy` (e.g., 01-Jan-2015)

## Requirements

- Java 11 or higher
- Maven 3.6+

## Dependencies

- **RxJava 1.2.10**: Reactive programming framework
- **RxJava String 1.1.1**: String processing utilities for RxJava
- **Spring JDBC 5.3.10**: Database connectivity
- **H2 Database 1.4.190**: In-memory database
- **Google Guava 31.0.1**: Caching and utilities
- **JUnit 4.11**: Testing framework
- **AssertJ 3.2.0**: Fluent assertions for testing

## Building and Running

### Build the project
```bash
mvn clean compile
```

### Run tests
```bash
mvn test
```

### Run the application
```bash
mvn exec:java -Dexec.mainClass="com.luxoft.aggregator.StreamDriver"
```

The application will process the example input data and output results to the console.

## Configuration

### Input Files
- `src/main/resources/example_input.txt`: Sample input data
- `src/main/resources/large_input.txt`: Large dataset for testing
- `src/main/resources/input.txt`: Alternative input file

### Database Initialization
The database schema and sample data are automatically initialized from:
- `src/main/resources/initialize-schema.sql`

## Performance Characteristics

- **Memory Efficient**: Streaming architecture prevents OutOfMemory errors on large datasets
- **Low CPU Overhead**: Optimized calculation engine with minimal processing overhead
- **Caching**: Database multiplier lookups are cached for 5 seconds to reduce database load
- **Scalable**: Tested with multi-gigabyte input files

## Testing

The project includes comprehensive unit tests covering:
- Input parsing validation
- Date format handling
- Error condition handling
- Calculation accuracy

Run tests with:
```bash
mvn test
```

## Utilities

### MassDataGenerator
Generates large amounts of test data for performance testing:
```java
MassDataGenerator.createFromFile()
```

### FileSizeCalculator
Utility for calculating file sizes during testing.

## Error Handling

- Invalid input lines are captured in a separate error stream
- Malformed dates and prices are logged but don't stop processing
- Database connection issues are handled gracefully
- Non-business dates (weekends) are automatically filtered out

## Output

The application produces:
- Console output with calculation results
- `src/main/resources/multiplied.txt`: Processed prices with multipliers applied
- Error reporting for failed parsing attempts

## Design Decisions

1. **RxJava Choice**: Selected for its mature ecosystem, excellent backpressure handling, and rich set of operators for stream processing
2. **In-Memory Database**: H2 provides fast access for multiplier lookups while maintaining simplicity
3. **Caching Strategy**: 5-second cache TTL balances performance with data freshness requirements
4. **Streaming Architecture**: Prevents memory issues when processing large files by avoiding loading entire datasets into memory
5. **Business Date Validation**: Ensures only valid trading days are processed (Monday-Friday)

## Future Enhancements

- Support for additional date formats
- Configurable calculation rules
- Real-time data stream processing
- Enhanced error reporting and monitoring
- Support for multiple database backends

## License

This project is developed for educational and evaluation purposes.