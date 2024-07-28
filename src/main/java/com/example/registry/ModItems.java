package com.example.registry;

import com.example.EndDragonMOD;
import com.example.special.Item.CommonItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item ENDER_EGG = registerItem("ender_egg",new CommonItem(new Item.Settings(),"这是末影龙的信物，拿着就会被末影龙追求到天涯海角", Formatting.GRAY));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(EndDragonMOD.MOD_ID, name), item);
    };

    public static void main_registerItem(){

    }
}
