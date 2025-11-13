# CLAUDE.md - AI Assistant Guide

This document provides comprehensive guidance for AI assistants working with the Financial Instrument Price Aggregator codebase.

## Project Overview

**Type**: Java Maven Project
**Purpose**: High-performance financial instrument price data aggregator
**Architecture**: Reactive streaming with RxJava
**Java Version**: 11
**Build Tool**: Maven 3.6+

This application processes financial instrument price data from CSV files and performs statistical calculations (mean, max, sum) using reactive programming principles. It's designed to handle large datasets (multi-gigabyte files) efficiently with low memory consumption.

## Repository Structure

```
aggregator/
├── src/
│   ├── main/
│   │   ├── java/com/luxoft/aggregator/
│   │   │   ├── StreamDriver.java           # Main orchestrator & entry point
│   │   │   ├── Aggregator.java             # Stream management & parsing
│   │   │   ├── InstrumentPrice.java        # Core data model
│   │   │   ├── InstrumentPriceParser.java  # CSV parsing logic
│   │   │   ├── InstrumentPriceUtilities.java # Statistical calculations
│   │   │   ├── MultiplierProvider.java     # Database + cache layer
│   │   │   ├── Either.java                 # Functional error handling
│   │   │   ├── Tuple.java                  # Pair container
│   │   │   ├── Flusher.java                # Output stream handler
│   │   │   ├── MassDataGenerator.java      # Test data generator
│   │   │   └── util/                       # Utility classes
│   │   └── resources/
│   │       ├── initialize-schema.sql       # H2 database schema
│   │       ├── example_input.txt           # Sample data
│   │       ├── large_input.txt             # Performance test data
│   │       └── input.txt                   # Alternative input
│   └── test/
│       └── java/com/luxoft/aggregator/
│           ├── InstrumentPriceParserTest.java
│           ├── InstrumentPriceTest.java
│           └── InstrumentPriceUtilitiesTest.java
├── pom.xml                                 # Maven configuration
└── README.md                               # User documentation
```

## Core Architecture & Components

### Data Flow Pipeline

1. **Input** → CSV files read as streams
2. **Parsing** → `InstrumentPriceParser` converts lines to `InstrumentPrice` objects
3. **Validation** → Business date filtering (weekends removed)
4. **Enrichment** → Price multipliers applied from database
5. **Calculation** → Statistical operations performed
6. **Output** → Results to console and files

### Key Components

#### StreamDriver (src/main/java/com/luxoft/aggregator/StreamDriver.java:16)
- **Role**: Main orchestrator and application entry point
- **Responsibilities**:
  - Coordinates data processing pipeline
  - Defines business logic for calculations
  - Combines multiple observables for final output
- **Key Methods**:
  - `run()`: Main execution method
  - `checkNonFutureAndBusinessDay()`: Filters weekends and future dates
  - `meanOfInstr1()`, `meanOfInstr2()`, `maxOfInstr3()`, `sumOfMostNewInstrumentPrices()`: Calculation methods

#### Aggregator (src/main/java/com/luxoft/aggregator/Aggregator.java:14)
- **Role**: Stream management and parsing coordination
- **Pattern**: Uses `ConnectableObservable` for hot observable pattern
- **Key Features**:
  - Lazy evaluation with `Observable.defer()`
  - Splits stream into success/failure paths using `Either`
  - Provides `attach()` method for functional composition

#### InstrumentPrice (src/main/java/com/luxoft/aggregator/InstrumentPrice.java:10)
- **Role**: Immutable data model
- **Fields**: `name` (String), `date` (LocalDate), `price` (BigDecimal)
- **Pattern**: Value object with proper equals/hashCode
- **Key Method**: `multiply(BigDecimal)` for applying multipliers

#### MultiplierProvider (src/main/java/com/luxoft/aggregator/MultiplierProvider.java:15)
- **Role**: Database access layer with caching
- **Pattern**: Cache-aside with Google Guava LoadingCache
- **Cache TTL**: 5 seconds
- **Technology**: Spring JdbcTemplate + H2 in-memory database

#### InstrumentPriceUtilities (src/main/java/com/luxoft/aggregator/InstrumentPriceUtilities.java:25)
- **Role**: Statistical calculations and predicates
- **Key Functions**:
  - `mean()`: Average calculation using reduce
  - `max()`: Maximum value finder
  - `mostRelevant()`: Top-N selection with PriorityQueue
  - `enrichPrice()`: Multiplier application
  - `instrument()`, `date()`: Functional predicates for filtering

### Functional Programming Patterns

#### Either<L, R> (src/main/java/com/luxoft/aggregator/Either.java:9)
- **Purpose**: Error handling without exceptions in stream processing
- **Convention**: Left = success, Right = error
- **Usage**: Parsing results split into valid prices vs. parsing errors

#### Tuple<A, B> (src/main/java/com/luxoft/aggregator/Tuple.java:7)
- **Purpose**: Pair container for multi-value returns
- **Fields**: `_1` and `_2` (public final)
- **Usage**: Accumulator patterns in reduce operations

## Code Conventions

### Naming Conventions
- **Classes**: PascalCase (e.g., `InstrumentPrice`, `MultiplierProvider`)
- **Methods**: camelCase (e.g., `parseFrom`, `multiplierFor`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `DATE_FORMATTER`)
- **Variables**: camelCase (e.g., `validPrices`, `instrumentPredicate`)

### Date Handling
- **Format**: `dd-MMM-yyyy` (e.g., "01-Jan-2015")
- **Type**: Always use `java.time.LocalDate` (not java.util.Date)
- **Formatter**: Use `InstrumentPriceUtilities.DATE_FORMATTER`
- **Locale**: Always use `Locale.ENGLISH` for month parsing

### Numeric Precision
- **Type**: Always use `java.math.BigDecimal` for prices and calculations
- **Division**: Always specify `MathContext.DECIMAL64` for precision
- **Parsing**: Use `new BigDecimal(String)` constructor for accuracy

### RxJava Patterns
- **Hot Observables**: Use `ConnectableObservable` with `publish()`
- **Lazy Evaluation**: Wrap in `Observable.defer()` for proper timing
- **Filtering**: Use functional predicates with `filter()`
- **Transformations**: Use `map()` for 1-to-1, `flatMap()` for 1-to-many
- **Aggregations**: Use `reduce()` for accumulation patterns
- **Error Handling**: Use `Either` pattern instead of `onError`

### Database Patterns
- **Connection**: Use `javax.sql.DataSource` abstraction
- **Queries**: Use Spring's `JdbcTemplate` with parameterized queries
- **Caching**: Always cache database lookups with appropriate TTL
- **Initialization**: Schema and data loaded from SQL files in resources

### Testing Patterns
- **Framework**: JUnit 4.11
- **Assertions**: AssertJ 3.2.0 (fluent assertions)
- **Naming**: Test method names describe behavior (e.g., `returnsRightInCaseOfInvalidData`)
- **Structure**: Arrange-Act-Assert pattern

## Development Workflow

### Building the Project
```bash
mvn clean compile
```

### Running Tests
```bash
mvn test
```

### Running the Application
```bash
mvn exec:java -Dexec.mainClass="com.luxoft.aggregator.StreamDriver"
```

### Modifying Input Data
- Default input controlled in `StreamDriver.main()` (lines 40-48)
- File paths: `src/main/resources/{example_input.txt, input.txt, large_input.txt}`
- Format: `INSTRUMENT_NAME,DATE,VALUE` (CSV)

### Common Maven Commands
```bash
mvn clean              # Clean build artifacts
mvn compile            # Compile without running tests
mvn test               # Run all tests
mvn package            # Create JAR (if packaging configured)
mvn dependency:tree    # View dependency hierarchy
```

## Testing Practices

### Test File Locations
- `src/test/java/com/luxoft/aggregator/InstrumentPriceParserTest.java`
- `src/test/java/com/luxoft/aggregator/InstrumentPriceTest.java`
- `src/test/java/com/luxoft/aggregator/InstrumentPriceUtilitiesTest.java`

### Writing Tests
```java
@Test
public void descriptiveTestName() throws Exception {
    // Arrange
    InstrumentPriceParser parser = new InstrumentPriceParser();

    // Act
    Either<InstrumentPrice, Tuple<String, Exception>> result =
        parser.call("INSTRUMENT1,01-Jan-2015,10.50");

    // Assert
    assertThat(result.isLeft()).isTrue();
    assertThat(result.left().getName()).isEqualTo("INSTRUMENT1");
}
```

### Test Data Generation
- Use `MassDataGenerator.createFromFile()` for large datasets
- Use `util/InstrumentFileGenerator` for custom test data
- Use `util/FileSizeCalculator` to measure generated file sizes

## Important Files and Locations

### Configuration Files
- `pom.xml`: Maven dependencies and build configuration
- `src/main/resources/initialize-schema.sql`: Database schema and seed data

### Input Files
- `src/main/resources/example_input.txt`: Small sample dataset
- `src/main/resources/large_input.txt`: Large performance test dataset
- `src/main/resources/input.txt`: Alternative input file

### Output Files
- `src/main/resources/multiplied.txt`: Generated file with multiplied prices

### Key Source Files to Understand First
1. `InstrumentPrice.java` - Data model
2. `InstrumentPriceParser.java` - Parsing logic
3. `Aggregator.java` - Stream coordination
4. `StreamDriver.java` - Business logic
5. `InstrumentPriceUtilities.java` - Calculations

## AI Assistant Guidelines

### When Adding New Features

1. **Understand the Stream Flow**: Always consider how RxJava observables flow through the system
2. **Maintain Immutability**: Never mutate `InstrumentPrice` objects; create new instances
3. **Use Functional Patterns**: Prefer `filter()`, `map()`, `reduce()` over imperative loops
4. **Handle Errors with Either**: Don't throw exceptions in stream processing; use `Either` pattern
5. **Test with Large Data**: Verify memory efficiency with multi-GB test files

### When Modifying Calculations

1. **Location**: Add calculation methods to `InstrumentPriceUtilities.java`
2. **Pattern**: Follow the functional predicate pattern (see `instrument()`, `date()`)
3. **Return Type**: Use `Observable<Optional<BigDecimal>>` for nullable results
4. **Composition**: Wire calculations in `StreamDriver.run()` using `combineLatest()`

### When Working with Database

1. **Schema Changes**: Update `initialize-schema.sql`
2. **Cache Invalidation**: Consider when to call `invalidateCachedInstrument()`
3. **Connection Safety**: Always use `DataSource` abstraction, never raw JDBC
4. **Query Safety**: Use parameterized queries to prevent SQL injection

### When Adding Dependencies

1. **Update**: `pom.xml` dependencies section
2. **Compatibility**: Ensure Java 11 compatibility
3. **Version Strategy**: Use stable versions; avoid SNAPSHOT dependencies
4. **Scope**: Use `<scope>test</scope>` for test-only dependencies

### Common Pitfalls to Avoid

1. **Don't use `java.util.Date`**: Always use `java.time.LocalDate`
2. **Don't use `double` for money**: Always use `BigDecimal`
3. **Don't call `connect()` multiple times**: Hot observable can only connect once
4. **Don't forget `Observable.defer()`**: Required for lazy evaluation of observables
5. **Don't parse dates without locale**: Always specify `Locale.ENGLISH`
6. **Don't create observables in constructors**: Use lazy initialization
7. **Don't forget MathContext**: Always specify precision for BigDecimal division

### Code Review Checklist

- [ ] No mutable state in stream processing
- [ ] BigDecimal used for all monetary calculations
- [ ] LocalDate used for all date handling
- [ ] Proper error handling with Either pattern
- [ ] Tests added for new functionality
- [ ] Date parsing uses correct formatter and locale
- [ ] Database queries are parameterized
- [ ] Cache invalidation considered
- [ ] Memory efficiency maintained (streaming, not loading full data)
- [ ] Javadoc added for public methods

### Performance Considerations

1. **Memory**: Stream processing prevents loading entire dataset into memory
2. **CPU**: Calculation overhead is minimal; focus on stream composition efficiency
3. **Database**: 5-second cache TTL reduces database load
4. **I/O**: Use buffered streams for file operations
5. **Testing**: Always test with `large_input.txt` or generated mass data

### Debugging Tips

1. **Stream Issues**: Use `doOnNext()` to log values flowing through observables
2. **Parsing Errors**: Check `failedToParse` stream output
3. **Date Problems**: Verify `DATE_FORMATTER` locale and pattern
4. **Calculation Issues**: Test predicates independently before composing
5. **Database Issues**: Enable H2 console for debugging (modify DataSource URL)

## Business Logic Reference

### Instrument-Specific Calculations

| Instrument | Calculation | Filter/Conditions |
|-----------|-------------|-------------------|
| INSTRUMENT1 | Mean of all prices | None |
| INSTRUMENT2 | Mean of prices | November 2014 only |
| INSTRUMENT3 | Maximum price | None |
| Others | Sum of 10 most recent | By date (most recent) |

### Date Validation Rules

- **Business Days Only**: Monday-Friday (ordinal 0-4)
- **Current Date**: Fixed at 2014-12-19 in `StreamDriver.checkNonFutureAndBusinessDay()`:53
- **Future Dates**: Filtered out (rejected if after current date)
- **Weekends**: Automatically removed

### Price Multipliers (Database)

| Instrument | Multiplier |
|-----------|-----------|
| INSTRUMENT1 | 1.05 |
| INSTRUMENT2 | 1.10 |
| INSTRUMENT3 | 1.15 |
| INSTRUMENT5 | 2.00 |
| Others | No multiplier applied |

## Git Workflow

### Branch Strategy
- Main development happens on feature branches
- Branch naming: `claude/claude-md-<session-id>`
- Always push to the specified feature branch

### Commit Guidelines
1. **Stage Changes**: `git add <files>`
2. **Commit**: `git commit -m "Clear, concise message"`
3. **Push**: `git push -u origin <branch-name>`

### Common Git Commands
```bash
git status                    # Check working directory status
git diff                      # See unstaged changes
git log --oneline -10         # View recent commits
git branch                    # List branches
git fetch origin <branch>     # Fetch specific branch
```

## Dependencies Reference

| Dependency | Version | Purpose |
|-----------|---------|---------|
| RxJava | 1.2.10 | Reactive stream processing |
| RxJava String | 1.1.1 | String utilities for RxJava |
| Spring JDBC | 5.3.10 | Database connectivity |
| H2 Database | 1.4.190 | In-memory database |
| Google Guava | 31.0.1-jre | Caching utilities |
| JUnit | 4.11 | Testing framework |
| AssertJ | 3.2.0 | Fluent assertions |

## Quick Reference Commands

### Start Working on This Project
1. Check current branch: `git status`
2. Build project: `mvn clean compile`
3. Run tests: `mvn test`
4. Run application: `mvn exec:java -Dexec.mainClass="com.luxoft.aggregator.StreamDriver"`

### Adding a New Calculation
1. Add method to `InstrumentPriceUtilities.java`
2. Wire in `StreamDriver.run()` using `combineLatest()`
3. Add tests in `InstrumentPriceUtilitiesTest.java`
4. Run tests: `mvn test`

### Modifying Database Schema
1. Update `src/main/resources/initialize-schema.sql`
2. Clear any cached H2 database files
3. Restart application to reload schema

---

**Last Updated**: 2025-11-13
**Document Version**: 1.0
**Project Version**: 0.1-SNAPSHOT
