package org.bpmnwithactiviti.chapter6.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

public class EndCandidateSubprocessListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String name = (execution.getCurrentActivityName() == null) ? "EndCandidateSubprocess" : execution.getCurrentActivityName();
        System.out.println(execution.getProcessDefinitionId() + ":" + execution.getProcessInstanceId() + "\t" + name);;
    }
}