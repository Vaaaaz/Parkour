package com.github.vaaaaz.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Itens {
    private ItemStack itemStack;
    private ItemMeta itemMeta;


    public Itens(Material material, int qunatidade, int data) {
        itemStack = new ItemStack(material, qunatidade, (short) data);
        itemMeta = itemStack.getItemMeta();
    }

    public Itens nome(String nome) {
        itemMeta.setDisplayName(nome);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public Itens lore(List<String> lore) {
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public static ItemStack criarItem(Material material, String nome, String lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta stack2 = stack.getItemMeta();
        List<String> ls = new ArrayList();
        ls.add(lore);
        stack2.setLore(ls);
        stack2.setDisplayName(nome);
        stack.setItemMeta(stack2);
        return stack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
