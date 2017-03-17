/**
 * Copyright 2017 Ambud Sharma
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.srotya.monitoring.kafka.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.srotya.monitoring.kafka.KafkaMonitorConfiguration;
import com.srotya.monitoring.kafka.core.kafka.KafkaOffsetMonitor;
import com.srotya.monitoring.kafka.core.managed.ZKClient;
import com.srotya.monitoring.kafka.util.KafkaConsumerOffsetUtil;

/**
 * @author ambud
 */
@Path("/metrics")
public class PrometheusResource {

	private KafkaMonitorConfiguration kafkaConfiguration;
	private ZKClient zkClient;

	public PrometheusResource(KafkaMonitorConfiguration kafkaConfiguration, ZKClient zkClient) {
		this.kafkaConfiguration = kafkaConfiguration;
		this.zkClient = zkClient;
		KafkaConsumerOffsetUtil kafkaConsumerOffsetUtil = KafkaConsumerOffsetUtil.getInstance(kafkaConfiguration,
				zkClient);
		kafkaConsumerOffsetUtil.setupMonitoring();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public String getKafkaOffsets() {
		KafkaConsumerOffsetUtil kafkaConsumerOffsetUtil = KafkaConsumerOffsetUtil.getInstance(kafkaConfiguration,
				zkClient);
		List<KafkaOffsetMonitor> kafkaOffsetMonitors = new ArrayList<>(kafkaConsumerOffsetUtil.getReferences().get());
		for (KafkaOffsetMonitor mon : kafkaConsumerOffsetUtil.getNewConsumer().values()) {
			kafkaOffsetMonitors.add(mon);
		}
		return KafkaConsumerOffsetUtil.toPrometheusFormat(kafkaOffsetMonitors);
	}

}
