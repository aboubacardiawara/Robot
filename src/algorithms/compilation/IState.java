package algorithms.compilation;

import java.util.function.Supplier;

public interface IState {
    boolean hasNext();
    IState next() throws TransitionConditionNotMetException;

    void setNext(IState state);

    void setTransitionCondition(Supplier<Boolean> transitionCondition);

    void setTransitionAction(Supplier<Void> transitionAction);

    boolean transitionConditionMet();

    void performsAction();
}
