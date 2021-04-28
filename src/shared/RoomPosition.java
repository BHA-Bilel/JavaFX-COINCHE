package shared;

public enum RoomPosition {
    BOTTOM, RIGHT, TOP, LEFT;

    public static RoomPosition getPositionBasedOnPlayerPosition(RoomPosition my_pos, RoomPosition position) {
        return switch (my_pos) {
            case BOTTOM -> position;
            case LEFT -> position.next();
            case TOP -> position.next().next();
            case RIGHT -> position.next().next().next();
        };
    }

    public RoomPosition teammate_with() {
        return switch (this) {
            case BOTTOM -> TOP;
            case LEFT -> RIGHT;
            case TOP -> BOTTOM;
            case RIGHT -> LEFT;
        };
    }

    public static RoomPosition getPositionByPlayerID(int playerID) {
        // get your position based on your playerID (not translated to bottom)
        if (playerID == 1) {
            return BOTTOM;
        } else if (playerID == 2) {
            return RIGHT;
        } else if (playerID == 3) {
            return TOP;
        } else {
            return LEFT;
        }
    }

    public RoomPosition next() {
        return switch (this) {
            case BOTTOM -> RIGHT;
            case RIGHT -> TOP;
            case TOP -> LEFT;
            case LEFT -> BOTTOM;
        };
    }

    public static RoomPosition getCurrentPositionByPlayerID(RoomPosition yourPosition, int position) {
        // relevant to player position (translated position)
        switch (yourPosition) {
            case BOTTOM -> {
                if (position == 1)
                    return BOTTOM;
                else if (position == 2) {
                    return RIGHT;
                } else if (position == 3) {
                    return TOP;
                } else {
                    return LEFT;
                }
            }
            case RIGHT -> {
                if (position == 1)
                    return LEFT;
                else if (position == 2) {
                    return BOTTOM;
                } else if (position == 3) {
                    return RIGHT;
                } else {
                    return TOP;
                }
            }
            case TOP -> {
                if (position == 1)
                    return TOP;
                else if (position == 2) {
                    return LEFT;
                } else if (position == 3) {
                    return BOTTOM;
                } else {
                    return RIGHT;
                }
            }
            case LEFT -> {
                if (position == 1)
                    return RIGHT;
                else if (position == 2) {
                    return TOP;
                } else if (position == 3) {
                    return LEFT;
                } else {
                    return BOTTOM;
                }
            }
        }
        return null;
    }
}
