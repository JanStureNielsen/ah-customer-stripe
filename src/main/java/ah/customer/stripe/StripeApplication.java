package ah.customer.stripe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StripeApplication {

    public static final String CSV_FILE_PATH_PREFIX = "src/main/resources/";

    //    // These days people prefer a Autowired constructor. I did this for speed to make this example.
    //    @Autowired
    //    private CellPhoneRepo cellPhoneRepo;
    //
    //    @Autowired
    //    private CellUsageMonthRepo cellUsageMonthRepo;
    //
    //    @Autowired
    //    private WcfReportHeaderRepo headerRepo;
    //
    //    @Autowired
    //    private WcfReportDetailRepo detailRepo;
    //
    //    @Autowired
    //    private WcfReportRepo reportRepo;
    //
    //    private CsvMapper csvMapper = new CsvMapper();

    public static void main(String[] args) {
        SpringApplication.run(StripeApplication.class, args);
    }


    //    @Bean
    //    public RestTemplate restTemplate(RestTemplateBuilder builder) {
    //        return builder.build();
    //    }
    //
    //    @Bean
    //    public OpenAPI customOpenAPI(@Value("${application-description}") String appDesciption,
    //                                 @Value("${application-version}") String appVersion) {
    //        return new OpenAPI()
    //                .info(new Info()
    //                        .title("sample application API")
    //                        .version(appVersion)
    //                        .description(appDesciption)
    //                        .termsOfService("http://swagger.io/terms/")
    //                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    //    }

    //    @PostConstruct
    //    public void setup() {
    //        // To parse a value into LocalDate from an input String.  And other date types for that matter.
    //        csvMapper.registerModule(new JavaTimeModule());
    //    }
    //
    //    // Show report upon startup of the application
    //    @EventListener(ApplicationReadyEvent.class)
    //    public void doSomethingAfterStartup() {
    //        readFileIntoDatabase("CellPhone.csv", CellPhone.class, cellPhoneRepo);
    //        readFileIntoDatabase("CellPhoneUsageByMonth.csv", CellUsageMonth.class, cellUsageMonthRepo);
    //
    //        final WcfReport report = reportRepo.getReport();
    //        report.showReport();
    //    }
    //
    //    // Read a sfile and write a list of Entities to the database.
    //    private <T> void readFileIntoDatabase(String csvFilename, Class<T> entityClass, CrudRepository<T, ?> repo) {
    //        try {
    //            final List<T> entities = readWholeFileIntoList(csvFilename, entityClass);
    //            final Iterable<T> s = repo.saveAll(entities);
    //        } catch (Exception e) {
    //            throw new IllegalArgumentException(e);
    //        }
    //    }
    //
    //    // Read the input file into a list of Entities.
    //    private <T> List<T> readWholeFileIntoList(String csvFilename, Class<T> entityClass) throws IOException {
    //        final List<T> entities = new ArrayList<>();
    //        final CsvSchema schema = CsvSchema.emptySchema().withHeader();
    //        final ObjectReader oReader = csvMapper.reader(entityClass).with(schema);
    //        try (Reader reader = new FileReader(CSV_FILE_PATH_PREFIX + csvFilename)) {
    //            final MappingIterator<T> objectMappingIterator = oReader.readValues(reader);
    //            while (objectMappingIterator.hasNext()) {
    //                final T next = objectMappingIterator.next();
    //                entities.add(next);
    //            }
    //        }
    //        return entities;
    //    }
}
