package de.byteagenten.ldr2;

import java.util.*;

/**
 * Created by knooma2e on 22.07.2016.
 */
public class ScopeStack {

    private ScopeStack parent;

    public ScopeStack() {
    }

    public ScopeStack(ScopeStack parent) {
        this.parent = parent;
    }


    private List<Scope> scopes = new ArrayList<>();

    public void push(Scope scope) {
        this.scopes.add(0, scope);
    }

    public Scope pop() {
        if (this.size() == 0) throw new IllegalStateException("");
        return this.scopes.remove(0);
    }

    public Scope remove(String scopeName) {

        Scope scope = scopes.stream().filter(s -> s.getName().equalsIgnoreCase(scopeName)).findFirst().orElse(null);
        if (scope != null) {
            scopes.remove(scope);
        } else if (this.parent != null) {
            scope = this.parent.remove(scopeName);
        }

        return scope;
    }

    public int size() {
        return this.scopes.size();
    }

    public void clear() {
        this.scopes.clear();
    }

    private static List<Scope> collectScopes(ScopeStack scopeStack, List<Scope> scopeList) {

        scopeList.addAll(scopeStack.scopes);
        if (scopeStack.parent != null) collectScopes(scopeStack.parent, scopeList);
        return scopeList;
    }

    public List<Scope> all() {
        return Collections.unmodifiableList(collectScopes(this, new ArrayList<>()));
    }

    public ScopeStack createChild() {

        return new ScopeStack(this);
    }
}
