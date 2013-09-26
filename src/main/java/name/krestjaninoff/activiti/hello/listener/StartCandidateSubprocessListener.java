package org.bpmnwithactiviti.chapter6.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import static name.krestjaninoff.activiti.hello.Main.log;

public class StartCandidateSubprocessListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String name = (execution.getCurrentActivityName() == null) ? "StartCandidateSubprocess" : execution.getCurrentActivityName();
        log(execution.getProcessDefinitionId() + ":" + execution.getProcessInstanceId() + "\t" + name);

        //TODO: next strokes of code are for validation context variables renewal after process suspension
        execution.setVariableLocal("token", 42);
    }
}