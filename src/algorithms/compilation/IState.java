package algorithms.compilation;

import java.util.function.Supplier;

public interface IState {
    boolean hasNext();
    IState next() throws AnyTransitionConditionMetException;

    void addNext(IState state, Supplier<Boolean> transitionCondition);

    void addNext(IState state);

    void setStateAction(Supplier<Void> transitionAction);

    void performsAction();
}
