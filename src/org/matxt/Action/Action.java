package org.matxt.Action;

import org.matxt.Element.Element;

public class Action<T extends Element> {
    public interface ActionExec<E> {
        void apply (E element, E clone, float time);
    }

    public T element;
    public ActionExec<T> action;
    public float from, to;
    private T clone;

    public Action (T element, ActionExec<T> action, float from, float to) {
        this.element = element;
        this.action = action;
        this.from = from;
        this.to = to;
        this.clone = null;
    }

    public float getDuration () {
        return to - from;
    }

    public boolean isTimeInsideWindow (float time) {
        return time >= from && time <= to;
    }

    public void applyFunction (float time) {
        if (this.clone == null) {
            this.clone = (T) this.element.clone();
        }

        action.apply(element, clone, time);
    }
}
