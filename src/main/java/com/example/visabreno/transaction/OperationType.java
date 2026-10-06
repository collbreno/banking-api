package com.example.visabreno.transaction;

public enum OperationType {
    NORMAL_PURCHASE(1, -1),
    PURCHASE_WITH_INSTALLMENTS(2, -1),
    WITHDRAWAL(3, -1),
    CREDIT_VOUCHER(4, 1);

    private final int code;
    private final int sign;

    OperationType(int code, int sign) {
        this.code = code;
        this.sign = sign;
    }

    public int code() {
        return code;
    }

    public int sign() {
        return sign;
    }

    public static OperationType fromCode(int code) {
        for (OperationType op : values()) {
            if (op.code == code) {
                return op;
            }
        }

        throw new InvalidOperationTypeException(code);
    }
}
