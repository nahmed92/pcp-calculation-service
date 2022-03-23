package com.deltadental.pcp.calculation.scheduler;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deltadental.pcp.calculation.repos.ContractMemberClaimsRepo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PCPCalculationServiceScheduler {

	@Autowired
	PCPAssignmentTask pcpAssignmentTask;
	
	@Autowired
	private ContractMemberClaimsRepo contractMemberClaimsRepo; 
	
//	@Scheduled(initialDelay = 1000, fixedRate = 10000)
	public void schedulePCPAssignments() {
		long totalSeconds = 0l;
		List<String> distinctStates = contractMemberClaimsRepo.findDistinctStateWhereStatusIsNull();
		if(CollectionUtils.isNotEmpty(distinctStates)) {
			int distinctStatesSize = distinctStates.size();
			ExecutorService executor = Executors.newFixedThreadPool(distinctStatesSize);
			log.info("Number of fixed thread pool size : " + distinctStatesSize);
			for (String state : distinctStates) {

				Callable<Long> callable = new PCPCalculationServiceCallable(state);
				// submit Callable tasks to be executed by thread pool
				Future<Long> future = executor.submit(callable);
				try {
					long seconds = future.get();
					totalSeconds = totalSeconds + seconds;
					System.out.println("Total seconds : "+totalSeconds);
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
			executor.shutdown();
			try {
				executor.awaitTermination(60, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
				log.error(e.getLocalizedMessage());
			}
			System.out.println("Time to process all states : " + totalSeconds + "second[s]");
		} else {
			log.info("No claims to process for pcp assignments");
		}
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	private class PCPCalculationServiceCallable implements Callable<Long> {

		private String state;
		
		@Override
		public Long call() {
			String threadName = Thread.currentThread().getName();
			log.info("Thread # " + threadName + " is started this task");
			long seconds = pcpAssignmentTask.processPCPAssignment(this.state);
			log.info("Thread # " + threadName + " is finished this task in "+seconds+" second[s]");
			return seconds;
		}
	}
}
