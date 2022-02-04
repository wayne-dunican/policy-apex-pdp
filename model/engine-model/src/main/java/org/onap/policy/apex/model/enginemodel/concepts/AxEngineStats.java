/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020,2022 Nordix Foundation.
 *  Modifications Copyright (C) 2022 Bell Canada.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.model.enginemodel.concepts;

import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Getter;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * This class is a java bean that is used to record statistics on Apex engines as they execute. Statistics on the number
 * of events, the amount of time taken to execute the last policy, the average policy execution time, the up time of the
 * engine, and the time stamp of the last engine start are recorded.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "apexEngineStats", namespace = "http://www.onap.org/policy/apex-pdp")
@XmlType(name = "AxEngineStats", namespace = "http://www.onap.org/policy/apex-pdp", propOrder = {"key", "timeStamp",
    "eventCount", "lastExecutionTime", "averageExecutionTime", "upTime", "lastStart"})
public class AxEngineStats extends AxConcept {
    private static final long serialVersionUID = -6981129081962785368L;
    private static final int HASH_CODE_PRIME = 32;
    static final String ENGINE_INSTANCE_ID = "engine_instance_id";
    static final Gauge ENGINE_EVENTS_EXECUTED_COUNT = Gauge.build().name("apex_engine_events_executed_count")
            .labelNames(ENGINE_INSTANCE_ID)
            .help("Total number of APEX events processed by the engine.").register();
    static final Gauge ENGINE_UPTIME = Gauge.build().name("apex_engine_uptime")
            .labelNames(ENGINE_INSTANCE_ID)
            .help("Time elapsed since the engine was started.").register();
    static final Gauge ENGINE_START_TIMESTAMP = Gauge.build().name("apex_engine_last_start_timestamp_epoch")
            .labelNames(ENGINE_INSTANCE_ID)
            .help("Epoch timestamp of the instance when engine was last started.").register();
    static final Gauge ENGINE_AVG_EXECUTION_TIME = Gauge.build().name("apex_engine_average_execution_time_seconds")
            .labelNames(ENGINE_INSTANCE_ID)
            .help("Average time taken to execute an APEX policy in seconds.").register();
    static final Histogram ENGINE_LAST_EXECUTION_TIME = Histogram.build()
            .name("apex_engine_last_execution_time").labelNames(ENGINE_INSTANCE_ID)
            .help("Time taken to execute the last APEX policy in seconds.").register();

    @XmlElement(name = "key", required = true)
    private AxReferenceKey key;

    @XmlElement(required = true)
    private long timeStamp;

    @XmlElement(required = true)
    private long eventCount;

    @XmlElement(required = true)
    private long lastExecutionTime;

    @XmlElement(required = true)
    private double averageExecutionTime;

    @XmlElement(required = true)
    private long upTime;

    @Getter
    private transient long lastEnterTime;

    @XmlElement(required = true)
    private long lastStart;

    /**
     * The Default Constructor creates an engine statistics instance with a null key and with all values cleared.
     */
    public AxEngineStats() {
        this(new AxReferenceKey());
        timeStamp = 0;
        eventCount = 0;
        lastExecutionTime = 0;
        averageExecutionTime = 0;
        upTime = 0;
        lastEnterTime = 0;
        lastStart = 0;
        initEngineMetricsWithPrometheus();
    }

    /**
     * Register the APEX engine metrics with Prometheus.
     */
    private void initEngineMetricsWithPrometheus() {
        var engineId = getKey().getParentArtifactKey().getId();
        if (engineId.startsWith(AxKey.NULL_KEY_NAME)) {
            return;
        }
        ENGINE_UPTIME.labels(engineId).set(upTime / 1000d);
        ENGINE_EVENTS_EXECUTED_COUNT.labels(engineId).set(this.eventCount);
        ENGINE_START_TIMESTAMP.labels(engineId).set(this.lastStart);
        ENGINE_AVG_EXECUTION_TIME.labels(engineId).set(this.averageExecutionTime / 1000d);
        ENGINE_LAST_EXECUTION_TIME.labels(engineId).observe(this.lastExecutionTime / 1000d);
    }

    /**
     * Copy constructor.
     *
     * @param copyConcept the concept to copy from
     */
    public AxEngineStats(final AxEngineStats copyConcept) {
        super(copyConcept);
    }

    /**
     * The Keyed Constructor creates an engine statistics instance with the given key and all values cleared.
     *
     * @param key the key
     */
    public AxEngineStats(final AxReferenceKey key) {
        this(key, 0, 0, 0, 0, 0, 0);
    }

    /**
     * This Constructor creates an engine statistics instance with all its fields set.
     *
     * @param key the engine statistics key
     * @param timeStamp the time stamp at which the statistics were recorded
     * @param eventCount the number of events processed by the engine
     * @param lastExecutionTime the amount of time taken to execute the last policy
     * @param averageExecutionTime the average amount of time taken to execute a policy
     * @param upTime the time that has elapsed since the policy engine was started
     * @param lastStart the time at which the policy engine was last started
     */
    public AxEngineStats(final AxReferenceKey key, final long timeStamp, final long eventCount,
            final long lastExecutionTime, final double averageExecutionTime, final long upTime, final long lastStart) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");

        this.key = key;
        this.timeStamp = timeStamp;
        this.eventCount = eventCount;
        this.lastExecutionTime = lastExecutionTime;
        this.averageExecutionTime = averageExecutionTime;
        this.upTime = upTime;
        this.lastStart = lastStart;
        initEngineMetricsWithPrometheus();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxReferenceKey getKey() {
        return key;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<AxKey> getKeys() {
        return key.getKeys();
    }

    /**
     * Sets the engine statistics key.
     *
     * @param key the engine statistics key
     */
    public void setKey(final AxReferenceKey key) {
        Assertions.argumentNotNull(key, "key may not be null");
        this.key = key;
    }

    /**
     * Gets the time stamp at which the statistics were recorded.
     *
     * @return the time stamp at which the statistics were recorded
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Gets the time stamp at which the statistics were recorded as a string.
     *
     * @return the time stamp at which the statistics were recorded as a string
     */
    public String getTimeStampString() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(timeStamp);
    }

    /**
     * Sets the time stamp at which the statistics were recorded.
     *
     * @param timeStamp the time stamp at which the statistics were recorded
     */
    public void setTimeStamp(final long timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Gets the number of events processed by the engine.
     *
     * @return the number of events processed by the engine
     */
    public long getEventCount() {
        return eventCount;
    }

    /**
     * Sets the number of events processed by the engine.
     *
     * @param eventCount the number of events processed by the engine
     */
    public void setEventCount(final long eventCount) {
        this.eventCount = eventCount;
        ENGINE_EVENTS_EXECUTED_COUNT.labels(getKey().getParentArtifactKey().getId())
                .set(this.eventCount);
    }

    /**
     * Gets the amount of time taken to execute the last policy.
     *
     * @return the amount of time taken to execute the last policy
     */
    public long getLastExecutionTime() {
        return lastExecutionTime;
    }

    /**
     * Sets the amount of time taken to execute the last policy.
     *
     * @param lastExecutionTime the amount of time taken to execute the last policy
     */
    public void setLastExecutionTime(final long lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
        ENGINE_LAST_EXECUTION_TIME.labels(getKey().getParentArtifactKey().getId())
                .observe(this.lastExecutionTime / 1000d);
    }

    /**
     * Gets the average amount of time taken to execute a policy.
     *
     * @return the average amount of time taken to execute a policy
     */
    public double getAverageExecutionTime() {
        return averageExecutionTime;
    }

    /**
     * Sets the average amount of time taken to execute a policy.
     *
     * @param averageExecutionTime the average amount of time taken to execute a policy
     */
    public void setAverageExecutionTime(final double averageExecutionTime) {
        this.averageExecutionTime = averageExecutionTime;
        ENGINE_AVG_EXECUTION_TIME.labels(getKey().getParentArtifactKey().getId())
                .set(this.averageExecutionTime / 1000d);
    }

    /**
     * Gets the time that has elapsed since the policy engine was started.
     *
     * @return the time that has elapsed since the policy engine was started
     */
    public long getUpTime() {
        if (this.getLastStart() != 0) {
            return upTime + (timeStamp - this.getLastStart());
        }
        return upTime;
    }

    /**
     * Sets the time that has elapsed since the policy engine was started.
     *
     * @param upTime the time that has elapsed since the policy engine was started
     */
    public void setUpTime(final long upTime) {
        this.upTime = upTime;
        ENGINE_UPTIME.labels(getKey().getParentArtifactKey().getId()).set(this.upTime / 1000d);
    }

    /**
     * Sets the time at which the policy engine was last started.
     *
     * @param lastStart the time at which the policy engine was last started
     */
    private void setLastStart(final long lastStart) {
        this.lastStart = lastStart;
        ENGINE_START_TIMESTAMP.labels(getKey().getParentArtifactKey().getId()).set(this.lastStart);
    }

    /**
     * Gets the time at which the policy engine was last started.
     *
     * @return the time at which the policy engine was last started
     */
    public long getLastStart() {
        return lastStart;
    }

    /**
     * Resets all the statistic values to zero.
     */
    public synchronized void reset() {
        timeStamp = 0;
        eventCount = 0;
        lastExecutionTime = 0;
        averageExecutionTime = 0;
        upTime = 0;
        lastEnterTime = 0;
        lastStart = 0;
        initEngineMetricsWithPrometheus();
    }

    /**
     * Updates the statistics when called, used by the Apex engine when it starts executing a policy.
     *
     * @param eventkey the key of the event that is being executed
     */
    public synchronized void executionEnter(final AxArtifactKey eventkey) {
        final long now = System.currentTimeMillis();
        eventCount++;
        if (eventCount < 0) {
            eventCount = 2;
        }
        lastEnterTime = now;
        timeStamp = now;
        ENGINE_EVENTS_EXECUTED_COUNT.labels(getKey().getParentArtifactKey().getId()).set(this.eventCount);
    }

    /**
     * Updates the statistics when called, used by the Apex engine when it completes executing a policy.
     */
    public synchronized void executionExit() {
        final long now = System.currentTimeMillis();
        lastExecutionTime = now - lastEnterTime;
        ENGINE_LAST_EXECUTION_TIME.labels(getKey().getParentArtifactKey().getId())
                .observe(this.lastExecutionTime / 1000d);

        averageExecutionTime = ((averageExecutionTime * (eventCount - 1.0)) + lastExecutionTime) / eventCount;
        lastEnterTime = 0;
        timeStamp = System.currentTimeMillis();
        ENGINE_AVG_EXECUTION_TIME.labels(getKey().getParentArtifactKey().getId())
                .set(this.averageExecutionTime / 1000d);
    }

    /**
     * Updates the statistics when called, used by the Apex engine when it is started.
     */
    public synchronized void engineStart() {
        final long now = System.currentTimeMillis();
        timeStamp = now;
        this.setLastStart(now);
    }

    /**
     * Updates the statistics when called, used by the Apex engine when it is stopped.
     */
    public synchronized void engineStop() {
        final long now = System.currentTimeMillis();
        timeStamp = now;
        upTime += (timeStamp - this.getLastStart());
        this.setLastStart(0);
        ENGINE_UPTIME.labels(getKey().getParentArtifactKey().getId()).set(this.upTime / 1000d);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxValidationResult validate(final AxValidationResult result) {
        if (key.equals(AxReferenceKey.getNullKey())) {
            result.addValidationMessage(
                    new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID, "key is a null key"));
        }

        return key.validate(result);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clean() {
        key.clean();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(":(");
        builder.append("engineKey=");
        builder.append(key);
        builder.append(",timeStamp=");
        builder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(timeStamp));
        builder.append(",eventCount=");
        builder.append(eventCount);
        builder.append(",lastExecutionTime=");
        builder.append(lastExecutionTime);
        builder.append(",averageExecutionTime=");
        builder.append(averageExecutionTime);
        builder.append(",upTime=");
        builder.append(getUpTime());
        builder.append(")");
        return builder.toString();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxConcept copyTo(final AxConcept targetObject) {
        Assertions.argumentNotNull(targetObject, "target may not be null");

        final Object copyObject = targetObject;
        Assertions.instanceOf(copyObject, AxEngineStats.class);

        final AxEngineStats copy = ((AxEngineStats) copyObject);
        copy.setKey(new AxReferenceKey(key));
        copy.setTimeStamp(timeStamp);
        copy.setEventCount(eventCount);
        copy.setLastExecutionTime(lastExecutionTime);
        copy.setAverageExecutionTime(averageExecutionTime);
        copy.setUpTime(upTime);
        copy.setLastStart(lastStart);
        initEngineMetricsWithPrometheus();

        return copy;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + key.hashCode();
        result = prime * result + (int) (timeStamp ^ (timeStamp >>> HASH_CODE_PRIME));
        result = prime * result + (int) (eventCount ^ (eventCount >>> HASH_CODE_PRIME));
        result = prime * result + (int) (lastExecutionTime ^ (lastExecutionTime >>> HASH_CODE_PRIME));
        result = prime * result + ((int) averageExecutionTime ^ ((int) averageExecutionTime >>> HASH_CODE_PRIME));
        result = prime * result + (int) (upTime ^ (upTime >>> HASH_CODE_PRIME));
        result = prime * result + (int) (getLastStart() ^ (getLastStart() >>> HASH_CODE_PRIME));
        return result;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final AxEngineStats other = (AxEngineStats) obj;
        if (!key.equals(other.key)) {
            return false;
        }
        if (timeStamp != other.timeStamp) {
            return false;
        }
        if (eventCount != other.eventCount) {
            return false;
        }
        if (lastExecutionTime != other.lastExecutionTime) {
            return false;
        }
        if (Double.compare(averageExecutionTime, other.averageExecutionTime) != 0) {
            return false;
        }
        if (upTime != other.upTime) {
            return false;
        }
        return getLastStart() == other.getLastStart();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int compareTo(final AxConcept otherObj) {
        if (otherObj == null) {
            return -1;
        }
        if (this == otherObj) {
            return 0;
        }
        if (getClass() != otherObj.getClass()) {
            return this.hashCode() - otherObj.hashCode();
        }

        final AxEngineStats other = (AxEngineStats) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (timeStamp != other.timeStamp) {
            return (int) (timeStamp - other.timeStamp);
        }
        if (eventCount != other.eventCount) {
            return (int) (eventCount - other.eventCount);
        }
        if (lastExecutionTime != other.lastExecutionTime) {
            return (int) (lastExecutionTime - other.lastExecutionTime);
        }
        final int result = Double.compare(averageExecutionTime, other.averageExecutionTime);
        if (result != 0) {
            return result;
        }
        if (upTime != other.upTime) {
            return (int) (upTime - other.upTime);
        }

        return Long.compare(lastStart, other.lastStart);
    }
}
