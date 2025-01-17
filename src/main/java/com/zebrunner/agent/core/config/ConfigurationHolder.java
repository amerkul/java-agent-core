package com.zebrunner.agent.core.config;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConfigurationHolder {

    private static final ConfigurationProvidersChain CONFIGURATION_PROVIDERS_CHAIN
            = ConfigurationProvidersChain.getInstance();
    private static ReportingConfiguration configuration = CONFIGURATION_PROVIDERS_CHAIN.getConfiguration();

    public static void addConfigurationProviderAfter(ConfigurationProvider configurationProvider,
                                                     Class<? extends ConfigurationProvider> afterProviderWithClass) {
        List<ConfigurationProvider> configurationProviders = CONFIGURATION_PROVIDERS_CHAIN.getConfigurationProviders();

        boolean added = false;
        // no reason to check the latest provider because anyway we will add after it
        for (int i = 0; i < configurationProviders.size() - 1; i++) {
            ConfigurationProvider existingConfigurationProvider = configurationProviders.get(i);
            if (afterProviderWithClass.isInstance(existingConfigurationProvider)) {
                configurationProviders.add(i + 1, configurationProvider);
                added = true;
            }
        }

        if (!added) {
            configurationProviders.add(configurationProvider);
        }

        configuration = CONFIGURATION_PROVIDERS_CHAIN.getConfiguration();
    }

    public static boolean isReportingEnabled() {
        return configuration.isReportingEnabled();
    }

    public static String getProjectKey() {
        return configuration.getProjectKey();
    }

    public static String getHost() {
        return configuration.getServer().getHostname();
    }

    public static String getToken() {
        return configuration.getServer().getAccessToken();
    }

    public static String getRunDisplayNameOr(String displayName) {
        String RUN_DISPLAY_NAME = configuration.getRun().getDisplayName();
        return RUN_DISPLAY_NAME != null ? RUN_DISPLAY_NAME : displayName;
    }

    public static String getRunBuild() {
        return configuration.getRun().getBuild();
    }

    public static String getRunEnvironment() {
        return configuration.getRun().getEnvironment();
    }

    public static String getRunContext() {
        return Optional.ofNullable(System.getProperty("ci_run_id"))
                       .map(ConfigurationHolder::toSerializedRunContext)
                       .orElseGet(() -> configuration.getRun().getContext());
    }

    public static boolean shouldRetryKnownIssues() {
        Boolean retryKnownIssues = configuration.getRun().getRetryKnownIssues();
        return retryKnownIssues != null && retryKnownIssues;
    }

    public static boolean shouldSubstituteRemoteWebDrivers() {
        Boolean substituteRemoteWebDrivers = configuration.getRun().getSubstituteRemoteWebDrivers();
        return substituteRemoteWebDrivers != null && substituteRemoteWebDrivers;
    }

    public static boolean shouldTreatSkipsAsFailures() {
        Boolean treatSkipsAsFailures = configuration.getRun().getTreatSkipsAsFailures();
        return treatSkipsAsFailures == null || treatSkipsAsFailures;
    }

    public static String getTestCaseStatusOnPass() {
        return configuration.getRun().getTestCaseStatus().getOnPass();
    }

    public static String getTestCaseStatusOnFail() {
        return configuration.getRun().getTestCaseStatus().getOnFail();
    }

    public static String getTestCaseStatusOnSkip() {
        return configuration.getRun().getTestCaseStatus().getOnSkip();
    }

    private static String toSerializedRunContext(String ciRunId) {
        Map<String, Object> runContext = new HashMap<>();
        runContext.put("id", ciRunId);
        if ("true".equalsIgnoreCase(System.getProperty("rerun_failures"))) {
            runContext.put("rerunOnlyFailures", true);
            runContext.put("statuses", Arrays.asList("FAILED", "SKIPPED", "ABORTED", "IN_PROGRESS"));
        }
        return new Gson().toJson(runContext);
    }

//    private static String toSerializedRunContext(String ciRunId) {
//        Map<String, Object> runContext = new HashMap<>();
//        runContext.put("testRunUuid", ciRunId);
//        runContext.put("mode", "LEGACY");
//        if ("true".equalsIgnoreCase(System.getProperty("rerun_failures"))) {
//            Map<String, Object> rerunCriteria = new HashMap<>();
//            rerunCriteria.put("anyOfStatuses", Arrays.asList("FAILED", "SKIPPED", "ABORTED"));
//            rerunCriteria.put("knownIssue", false);
//
//            runContext.put("rerunCriteria", rerunCriteria);
//            runContext.put("mode", "RERUN");
//        }
//        return new Gson().toJson(runContext);
//    }

    public static boolean shouldNotifyOnEachFailure() {
        Boolean notifyOnEachFailure = configuration.getNotification().getNotifyOnEachFailure();
        return notifyOnEachFailure != null && notifyOnEachFailure;
    }

    public static String getSlackChannels() {
        return configuration.getNotification().getSlackChannels();
    }

    public static String getMsTeamsChannels() {
        return configuration.getNotification().getMsTeamsChannels();
    }

    public static String getEmails() {
        return configuration.getNotification().getEmails();
    }

    public static Long getMilestoneId() {
        return configuration.getMilestone().getId();
    }

    public static String getMilestoneName() {
        return configuration.getMilestone().getName();
    }

}
