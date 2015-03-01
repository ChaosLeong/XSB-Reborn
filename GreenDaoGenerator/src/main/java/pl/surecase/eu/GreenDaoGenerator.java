package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class GreenDaoGenerator {

    public static void main(String args[]) throws Exception {
        GreenDaoGenerator generator = new GreenDaoGenerator();
        Schema schema = new Schema(1, "com.sise.help.database");
        generator.addUserTable(schema);
        generator.addSessionTable(schema);
        generator.addPostTable(schema);
        new DaoGenerator().generateAll(schema, args[0]);
    }

    private void addSessionTable(Schema schema) {
        Entity session = schema.addEntity("ChatMessage");
        session.addIdProperty().primaryKey();
        session.addStringProperty("peerId").notNull();
        session.addStringProperty("otherPeerId").notNull();
        session.addBooleanProperty("isFrom").notNull();
        session.addStringProperty("msg").notNull();
        session.addLongProperty("timestamp").notNull();
    }

    private void addUserTable(Schema schema) {
        Entity user = schema.addEntity("User");
        user.addIdProperty().primaryKey();
        user.addStringProperty("peeId").notNull();
        user.addStringProperty("avatarUrl");
        user.addStringProperty("introduction");
        user.addStringProperty("gender");
        user.addStringProperty("area");
        user.addStringProperty("nickname");
        user.addIntProperty("score");
    }

    private void addPostTable(Schema schema) {

    }
}
