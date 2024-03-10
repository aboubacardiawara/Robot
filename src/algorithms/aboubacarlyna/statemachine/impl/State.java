package algorithms.aboubacarlyna.statemachine.impl;

import java.util.function.Supplier;

import algorithms.aboubacarlyna.statemachine.AnyTransitionConditionMetException;
import algorithms.aboubacarlyna.statemachine.interfaces.IState;

public class State implements IState {

    protected IState[] nextStates;
    protected Supplier<Boolean>[] transitionConditions;
    protected Runnable transitionAction;
    protected int cpt = 0;
    protected String description;
    public State(int stateCount) {
        this.nextStates = new IState[stateCount];
        this.transitionConditions = new Supplier[stateCount];
    }

    public State() {
        this(1);
        this.description = "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean hasNext() {
        for (int i = 0; i < nextStates.length; i++) {
            if (nextStates[i] != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IState next() throws AnyTransitionConditionMetException {

        for (int i = 0; i < transitionConditions.length; i++) {
            if (transitionConditions[i].get()) {
                return nextStates[i];
            }
        }
        throw new AnyTransitionConditionMetException();
    }

    @Override
    public void addNext(IState state, Supplier<Boolean> transitionCondition) {
        transitionConditions[cpt]=transitionCondition;
        nextStates[cpt] = state;
        cpt++;
    }

    @Override
    public void addNext(IState state) {
        this.addNext(state, () -> true);
    }

    @Override
    public void setStateAction(Runnable transitionAction) {
        this.transitionAction = transitionAction;
    }

    @Override
    public void performsAction() {
        this.transitionAction.run();
    }
}
