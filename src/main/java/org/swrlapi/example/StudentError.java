package org.swrlapi.example;

import java.util.Objects;

enum StudentErrorType {
    HIGH_PRIORITY_TO_LEFT,
    HIGH_PRIORITY_TO_RIGHT,
    LEFT_ASSOC_TO_LEFT,
    RIGHT_ASSOC_TO_RIGHT,
    IN_COMPLEX,
    SAME_OPERATION,
    STRICT_OPERANDS_ORDER
}

class StudentError {
    public StudentError(int errorPos, int reasonPos, StudentErrorType type) {
        ErrorPos = errorPos;
        ReasonPos = reasonPos;
        Type = type;
    }

    public int getErrorPos() {
        return ErrorPos;
    }

    public int getReasonPos() {
        return ReasonPos;
    }

    public StudentErrorType getType() {
        return Type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentError that = (StudentError) o;
        return getErrorPos() == that.getErrorPos() &&
                getReasonPos() == that.getReasonPos() &&
                getType() == that.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getErrorPos(), getReasonPos(), getType());
    }

    @Override
    public String toString() {
        return "StudentError{" +
                "ErrorPos=" + ErrorPos +
                ", ReasonPos=" + ReasonPos +
                ", Type=" + Type +
                '}';
    }

    int ErrorPos;
    int ReasonPos;
    StudentErrorType Type;
}
