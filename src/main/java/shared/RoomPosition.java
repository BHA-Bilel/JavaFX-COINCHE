package shared;

public enum RoomPosition {
    BOTTOM, RIGHT, TOP, LEFT;

    public RoomPosition nextPlayerToNotify() {
        switch (this) {
            case BOTTOM:
                return RIGHT;
            case RIGHT:
                return TOP;
            case TOP:
                return LEFT;
            default:
                return null;
        }
    }

    public static RoomPosition getPositionBasedOnPlayerPosition(RoomPosition my_pos, RoomPosition position) {
        switch (my_pos) {
            case BOTTOM:
                return position;
            case LEFT:
                return position.next();
            case TOP:
                return position.next().next();
            case RIGHT:
                return position.next().next().next();
            default:
                return null;
        }
    }

    public RoomPosition teammate_with() {
        switch (this) {
            case BOTTOM:
                return TOP;
            case LEFT:
                return RIGHT;
            case TOP:
                return BOTTOM;
            case RIGHT:
                return LEFT;
            default:
                return null;
        }
    }

    public static RoomPosition getPositionByPlayerID(int playerID) {
        // get your position based on your playerID (not translated to bottom)
        switch (playerID) {
            case 2:
                return RIGHT;
            case 3:
                return TOP;
            case 4:
                return LEFT;
            default: // case 1
                return BOTTOM;
        }
    }

    public RoomPosition next() {
        switch (this) {
            case BOTTOM:
                return RIGHT;
            case RIGHT:
                return TOP;
            case TOP:
                return LEFT;
            case LEFT:
                return BOTTOM;
            default:
                return null;
        }
    }

    public static RoomPosition getCurrentPositionByPlayerID(RoomPosition yourPosition, int position) {
        // relevant to player position (translated position)
        switch (yourPosition) {
            case BOTTOM: {
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
            case RIGHT: {
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
            case TOP: {
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
            case LEFT: {
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
