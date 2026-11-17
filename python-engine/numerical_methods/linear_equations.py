
# AquaPredict - Linear Equation Methods


# Create an Augmented Matrix
def create_matrix(A, B):

    n = len(B)

    if n == 0 or len(A) != n:
        raise ValueError("Invalid matrix dimensions.")

    for row in A:
        if len(row) != n:
            raise ValueError("Coefficient matrix must be square.")

    matrix = []

    for i in range(n):
        row = A[i][:] + [B[i]]
        matrix.append(row)

    return matrix


# Gauss Elimination Method
def gauss_elimination(A, B):

    matrix = create_matrix(A, B)
    n = len(B)

    # Forward Elimination with Partial Pivoting
    for i in range(n):

        # Find the largest pivot
        max_row = i

        for j in range(i + 1, n):
            if abs(matrix[j][i]) > abs(matrix[max_row][i]):
                max_row = j

        # Swap rows
        matrix[i], matrix[max_row] = matrix[max_row], matrix[i]

        if abs(matrix[i][i]) < 1e-12:
            raise ValueError("System has no unique solution.")

        # Eliminate values below pivot
        for j in range(i + 1, n):

            factor = matrix[j][i] / matrix[i][i]

            for k in range(i, n + 1):
                matrix[j][k] = matrix[j][k] - factor * matrix[i][k]

    # Back Substitution
    x = [0] * n

    for i in range(n - 1, -1, -1):

        total = matrix[i][n]

        for j in range(i + 1, n):
            total = total - matrix[i][j] * x[j]

        x[i] = total / matrix[i][i]

    return x


# Gauss-Jordan Method
def gauss_jordan(A, B):

    matrix = create_matrix(A, B)
    n = len(B)

    # Elimination with Partial Pivoting
    for i in range(n):

        # Find the largest pivot
        max_row = i

        for j in range(i + 1, n):
            if abs(matrix[j][i]) > abs(matrix[max_row][i]):
                max_row = j

        # Swap rows
        matrix[i], matrix[max_row] = matrix[max_row], matrix[i]

        if abs(matrix[i][i]) < 1e-12:
            raise ValueError("System has no unique solution.")

        # Make pivot equal to 1
        pivot = matrix[i][i]

        for j in range(n + 1):
            matrix[i][j] = matrix[i][j] / pivot

        # Make other values in pivot column equal to 0
        for j in range(n):

            if i != j:

                factor = matrix[j][i]

                for k in range(n + 1):
                    matrix[j][k] = matrix[j][k] - factor * matrix[i][k]

    # Extract solutions
    x = []

    for i in range(n):
        x.append(matrix[i][n])

    return x
