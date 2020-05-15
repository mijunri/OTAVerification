package juppaal;

public class UppaalTransition {
    private UppaalLocation source;
    private UppaalLocation target;
    private Label synchronisation;
    private Label guard;
    private Label assignment;

    public UppaalTransition(UppaalLocation source, UppaalLocation target,
                            Label synchronisation, Label guard, Label assignment) {
        this.source = source;
        this.target = target;
        this.synchronisation = synchronisation;
        this.guard = guard;
        this.assignment = assignment;
    }

    public UppaalLocation getSource() {
        return source;
    }

    public void setSource(UppaalLocation source) {
        this.source = source;
    }

    public UppaalLocation getTarget() {
        return target;
    }

    public void setTarget(UppaalLocation target) {
        this.target = target;
    }

    public Label getSynchronisation() {
        return synchronisation;
    }

    public void setSynchronisation(Label synchronisation) {
        this.synchronisation = synchronisation;
    }

    public Label getGuard() {
        return guard;
    }

    public void setGuard(Label guard) {
        this.guard = guard;
    }

    public Label getAssignment() {
        return assignment;
    }

    public void setAssignment(Label assignment) {
        this.assignment = assignment;
    }

    @Override
    public String toString() {
        return "UppaalTransition{" +
                "source=" + source +
                ", target=" + target +
                ", synchronisation=" + synchronisation +
                ", guard=" + guard +
                ", assignment=" + assignment +
                '}';
    }
}
