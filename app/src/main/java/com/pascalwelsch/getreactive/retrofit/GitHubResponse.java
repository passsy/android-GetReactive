package com.pascalwelsch.getreactive.retrofit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pascalwelsch on 10/24/15.
 */
public class GitHubResponse<T> {

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private boolean incompleteResults;

    private List<T> items = new ArrayList<>();

    private long totalCount;

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    /**
     * @return The items
     */
    public List<T> getItems() {
        return items;
    }

    /**
     * @return The totalCount
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * @return The incompleteResults
     */
    public boolean isIncompleteResults() {
        return incompleteResults;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    /**
     * @param incompleteResults The incomplete_results
     */
    public void setIncompleteResults(boolean incompleteResults) {
        this.incompleteResults = incompleteResults;
    }

    /**
     * @param items The items
     */
    public void setItems(List<T> items) {
        this.items = items;
    }

    /**
     * @param totalCount The total_count
     */
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}