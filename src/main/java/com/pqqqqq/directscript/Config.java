package com.pqqqqq.directscript;

import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.Sequencer;
import com.pqqqqq.directscript.lang.data.env.Variable;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.util.Optional;

/**
 * Created by Kevin on 2015-06-22.
 */
public class Config {
    private File file;
    private ConfigurationLoader<CommentedConfigurationNode> cfg;
    private DirectScript plugin = DirectScript.instance();

    Config(File file, ConfigurationLoader<CommentedConfigurationNode> cfg) {
        this.file = file;
        this.cfg = cfg;
    }

    public void init() {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            Lang lang = Lang.instance();
            lang.clear(); // Clear variables

            CommentedConfigurationNode root = cfg.load();
            CommentedConfigurationNode publicNode = root.getNode("public");

            lang.suppressNotifications(true); // Suppress notifications to save temporarily
            for (CommentedConfigurationNode variableNode : publicNode.getChildrenMap().values()) {
                Literal literal = Sequencer.instance().parse(variableNode.getNode("value").getString()).resolve(null);
                Optional<Literal.Types> type = Literal.Types.fromName(variableNode.getNode("type").getString());

                lang.addVariable(new Variable(variableNode.getKey().toString(), lang, literal, type));
            }
            lang.suppressNotifications(false); // Unsuppress them

            cfg.save(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveAll() {
        try {
            CommentedConfigurationNode root = cfg.createEmptyNode(ConfigurationOptions.defaults());
            CommentedConfigurationNode publicNode = root.getNode("public");

            for (Variable publicVariable : Lang.instance()) {
                publicVariable.save(publicNode.getNode(publicVariable.getName()));
            }

            cfg.save(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
