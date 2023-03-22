package kevintian.springbootdemo;

import org.slf4j.MDC;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.endpoints.CloudWatchLogsEndpointProvider;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityTracer {

    public static final String AUDIT_LOGS_GROUP_NAME = "";
    public static final String ERROR_LOGS_GROUP_NAME = "";

    private static ThreadLocal<ActivityTracer> threadLocalActivityTracer = new ThreadLocal<>();

    private List<InputLogEvent> auditInputLogEventList;

    private List<InputLogEvent> errorInputLogEventList;

    private String appName;

    private String apiName;

    private String userId;

    private String traceId;

    private long startTime;

    static CloudWatchLogsClient logsClient;

    static {
        CloudWatchLogsEndpointProvider cloudWatchLogsEndpointProvider = CloudWatchLogsEndpointProvider.defaultProvider();

        logsClient = CloudWatchLogsClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        "AKIA3XMORVFIBB6CPZXW",
                        "KPjFprqhaLAf/LnXixbMPHBNmVNGBKSMAGAh1Vsc")))
                .endpointProvider(cloudWatchLogsEndpointProvider)
                .overrideConfiguration(ClientOverrideConfiguration.builder().build())
                .endpointOverride(URI.create("https://logs.ap-southeast-1.amazonaws.com"))
                .region(Region.AP_SOUTHEAST_1)
                .build();
    }


    private static ActivityTracer getInstance(String appName, String apiName, String userId, String traceId) {

        if(ActivityTracer.threadLocalActivityTracer.get() == null) {
            ActivityTracer instance = new ActivityTracer(appName, apiName, userId, traceId);
            ActivityTracer.threadLocalActivityTracer.set(instance);
        }

        return ActivityTracer.threadLocalActivityTracer.get();
    }

    private ActivityTracer(String appName, String apiName, String userId, String traceId) {
        this.appName = appName;
        this.apiName = apiName;
        this.userId = userId;
        this.traceId = traceId;
        this.startTime = System.currentTimeMillis();
        this.auditInputLogEventList = new ArrayList<>();
        this.errorInputLogEventList = new ArrayList<>();
    }

    public void info(String desc) {
        Step step = new Step();
        step.setSTEP((this.auditInputLogEventList.size()+1) + ") " + desc);
        step.setTRACEID(this.traceId);
        step.setAPI(this.apiName);
        step.setUUID(this.userId);

        InputLogEvent inputLogEvent = InputLogEvent.builder()
                .message(JsonUtil.convertToString(step))
                .timestamp(System.currentTimeMillis())
                .build();

        this.auditInputLogEventList.add(inputLogEvent);
    }

    public void error(String desc) {
        Step step = new Step();
        step.setSTEP((this.auditInputLogEventList.size()+1) + ") " + desc);
        step.setTRACEID(this.traceId);
        step.setAPI(this.apiName);
        step.setUUID(this.userId);

        InputLogEvent inputLogEvent = InputLogEvent.builder()
                .message(JsonUtil.convertToString(step))
                .timestamp(System.currentTimeMillis())
                .build();

        this.auditInputLogEventList.add(inputLogEvent);
        this.errorInputLogEventList.add(inputLogEvent);
    }

    public static void flush() {
        ActivityTracer instance = ActivityTracer.threadLocalActivityTracer.get();

        if(instance != null) {
            instance.flushInstance();
            ActivityTracer.threadLocalActivityTracer.remove();
        }
    }

    private void flushInstance() {
        MDC.clear();

        Thread thread = new Thread(() -> {
            //logger.info("Starting thread to save logs into cloudwatch");

            String format = this.appName + "_%s_" +
                    (new SimpleDateFormat("yyyyMMdd").format(new Date(this.startTime)));

            try {
                //Save audit logs
                if(!this.auditInputLogEventList.isEmpty()) {
                    if(!saveLogs(this.auditInputLogEventList, AUDIT_LOGS_GROUP_NAME,
                            String.format(format, "audit_logs"))) {

                        createStreamAndSaveLogs(this.auditInputLogEventList, AUDIT_LOGS_GROUP_NAME,
                                String.format(format, "audit_logs"));
                    }
                }

                //Empty list
                this.auditInputLogEventList.clear();

                //Save error logs
                if(!this.errorInputLogEventList.isEmpty() &&
                        !saveLogs(this.errorInputLogEventList, ERROR_LOGS_GROUP_NAME,
                                String.format(format, "error_logs"))) {

                    createStreamAndSaveLogs(this.errorInputLogEventList, ERROR_LOGS_GROUP_NAME,
                            String.format(format, "error_logs"));
                }

                //Empty list
                this.errorInputLogEventList.clear();

                //Logging
                //logger.info("Time Taken : " + (System.currentTimeMillis() - startTime) + " ms.");
            }
            catch(Exception e) {
                //logger.error("Error while saving logs into cloudwatch", e);
            }

            //logger.info("Ending thread to save logs into cloudwatch");
        });

        thread.start();
    }

    boolean saveLogs(List<InputLogEvent> inputLogEventList, String logGroupName, String logStreamName) {

        try {
            PutLogEventsRequest putLogEventsRequest = PutLogEventsRequest.builder()
                    .logEvents(inputLogEventList)
                    .logGroupName(logGroupName)
                    .logStreamName(logStreamName)
                    .build();

            logsClient.putLogEvents(putLogEventsRequest);
            return true;
        }
        catch(ResourceNotFoundException e) {
            if(e.getMessage() != null && e.getMessage().contains("The specified log stream does not exist")) {
                //logger.info("Log stream \"" + logStreamName + "\" does not exist");
                return false;
            }
            else {
                throw e;
            }
        }
    }

    void createStreamAndSaveLogs(List<InputLogEvent> inputLogEventList, String logGroupName, String logStreamName) {

        //logger.info("Creating log stream \"" + logStreamName + "\"");

        logsClient.createLogStream(
                CreateLogStreamRequest.builder()
                        .logGroupName(logGroupName)
                        .logStreamName(logStreamName)
                        .build());

        if(!saveLogs(inputLogEventList, logGroupName, logStreamName)) {
            //logger.error("Failed to create log stream \"" + logStreamName + "\"" after attempt);
        }
    }

    public static void main(String[] args) {
        ActivityTracer activityTracer = ActivityTracer
                .getInstance("PE", "GET /users/profiles", "user-id", "trace-id");

        activityTracer.info("Run stuff");
        activityTracer.error("something went wrong");
        activityTracer.info("Run some stuff again");

        ActivityTracer.flush();
    }
}