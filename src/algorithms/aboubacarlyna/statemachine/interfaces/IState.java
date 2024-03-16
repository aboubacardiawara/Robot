package algorithms.aboubacarlyna.statemachine.interfaces;

import java.util.function.Supplier;

import algorithms.aboubacarlyna.statemachine.AnyTransitionConditionMetException;

public interface IState {
    boolean hasNext();

    IState next() throws AnyTransitionConditionMetException;

    void addNext(IState state, Supplier<Boolean> transitionCondition);

    void addNext(IState state);

    void setStateAction(Runnable transitionAction);

    void performsAction();

    void setDescription(String tourneVersLeNord);

    String dotify();
}
