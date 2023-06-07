package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StackImplTest {
    private StackImpl<Integer> stack;

    @BeforeEach
    void setUp() {
        stack = new StackImpl<>();
    }

    @Test
    void push() {
    }

    @Test
    @DisplayName("test that the pop method exists")
    void popExists() {
        assertNull(this.stack.pop());
    }

    @Test
    @DisplayName("test that the peek method exists")
    void peekExists() {
        assertNull(this.stack.peek());
    }

    @Test
    void size() {
    }
}