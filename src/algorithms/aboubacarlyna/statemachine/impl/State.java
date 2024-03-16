package algorithms.aboubacarlyna.statemachine.impl;

import java.util.Set;
import java.util.Stack;
import java.util.function.Supplier;

import algorithms.aboubacarlyna.statemachine.AnyTransitionConditionMetException;
import algorithms.aboubacarlyna.statemachine.interfaces.IState;

public class State implements IState {

    protected IState[] nextStates;
    protected Supplier<Boolean>[] transitionConditions;
    protected Runnable transitionAction;
    protected int cpt = 0;
    protected String description;

    @SuppressWarnings("unchecked")
    public State(int stateCount) {
        this.nextStates = new IState[stateCount];
        this.transitionConditions = (Supplier<Boolean>[]) new Supplier<?>[stateCount];
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
        transitionConditions[cpt] = transitionCondition;
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

    @Override
    public String toString() {
        return this.description;
    }

    private boolean anyConditionMeet() {
        for (int i = 0; i < transitionConditions.length; i++) {
            if (transitionConditions[i].get()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String dotify() {
        return "digraph G {\n" + dotifyAux(this, new java.util.HashSet<>()) + "}";
    }

    public String dotifyAux(State state, Set<State> visited) {
        String sb = "";
        if (visited.contains(state)) {
            return "";
        }
        visited.add(state);
        for (int i = 0; i < state.nextStates.length; i++) {
            if (state.nextStates[i] != null) {
                sb += state.toString() + " -> " + state.nextStates[i].toString() + " [label=\""
                        + "?" + "\"];\n";
                sb += dotifyAux((State) state.nextStates[i], visited);
            }
        }
        return sb.toString();
    }
}
