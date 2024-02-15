package algorithms.compilation;

import java.util.function.Supplier;

public class State implements IState {

    protected IState nextState;
    protected Supplier<Boolean> transitionConditionMet;
    private Supplier<Void> transitionAction;

    @Override
    public boolean hasNext() {
        return nextState != null;
    }

    @Override
    public IState next() throws TransitionConditionNotMetException {
        if (transitionConditionMet.get()) {
            return nextState;
        }
        throw new TransitionConditionNotMetException();
    }

    @Override
    public void setNext(IState state) {
        this.nextState = state;
    }

    @Override
    public void setTransitionCondition(Supplier<Boolean> transitionCondition) {
        this.transitionConditionMet = transitionCondition;
    }

    @Override
    public void setTransitionAction(Supplier<Void> transitionAction) {
        this.transitionAction = transitionAction;
    }

    @Override
    public boolean transitionConditionMet() {
        if (transitionAction != null) {
            transitionAction.get();
        }
        return true;
    }

    @Override
    public void performsAction() {
        this.transitionAction.get();
    }
}
