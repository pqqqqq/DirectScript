package com.pqqqqq.directscript;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.Sequencer;
import com.pqqqqq.directscript.lang.data.env.Variable;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;

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
            CommentedConfigurationNode root = cfg.load();
            CommentedConfigurationNode publicNode = root.getNode("public");

            plugin.suppressNotifications(true); // Suppress notifications to save temporarily
            for (CommentedConfigurationNode variableNode : publicNode.getChildrenMap().values()) {
                Literal literal = Sequencer.instance().parse(variableNode.getString()).resolve(null).get();
                plugin.addVariable(new Variable(variableNode.getKey().toString(), plugin, literal));
            }
            plugin.suppressNotifications(false); // Unsuppress them

            cfg.save(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void saveAll() {
        try {
            CommentedConfigurationNode root = cfg.createEmptyNode(ConfigurationOptions.defaults());
            CommentedConfigurationNode publicNode = root.getNode("public");

            for (Variable publicVariable : plugin) {
                publicNode.getNode(publicVariable.getName()).setValue(publicVariable.getDatum().get().toSequence());
            }

            cfg.save(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
