package net.atrophygames.obscrts.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class RuleBook {


    private ItemStack itemStack;
    private BookMeta bookMeta;

    public RuleBook() {
        itemStack = new ItemStack(Material.WRITTEN_BOOK);
        bookMeta = (BookMeta) itemStack.getItemMeta();
    }

    public RuleBook setTitle(String title) {
        bookMeta.setTitle(title);
        return this;
    }

    public RuleBook setAuthor(String author) {
        bookMeta.setAuthor(author);
        return this;
    }

    public RuleBook addPage(String page) {
        bookMeta.addPage(page);
        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(bookMeta);
        return itemStack;
    }
}
