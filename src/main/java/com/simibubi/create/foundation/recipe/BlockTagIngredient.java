package com.simibubi.create.foundation.recipe;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import com.simibubi.create.Create;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BlockTagIngredient implements CustomIngredient {
	protected final TagKey<Block> tag;

	protected BlockTagIngredient(TagKey<Block> tag) {
		this.tag = tag;
	}

	public static BlockTagIngredient create(TagKey<Block> tag) {
		return new BlockTagIngredient(tag);
	}

	public TagKey<Block> getTag() {
		return tag;
	}

	@Override
	public boolean requiresTesting() {
		return false;
	}

	@Override
	public List<ItemStack> getMatchingStacks() {
		ImmutableList.Builder<ItemStack> stacks = ImmutableList.builder();
		for (Holder<Block> block : BuiltInRegistries.BLOCK.getTagOrEmpty(tag)) {
			stacks.add(new ItemStack(block.value().asItem()));
		}
		return stacks.build();
	}

	@Override
	public boolean test(ItemStack stack) {
		return Block.byItem(stack.getItem()).defaultBlockState().is(tag);
	}

	@Override
	public CustomIngredientSerializer<?> getSerializer() {
		return Serializer.INSTANCE;
	}

	public static class Serializer implements CustomIngredientSerializer<BlockTagIngredient> {
		public static final ResourceLocation ID = Create.asResource("block_tag_ingredient");
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public ResourceLocation getIdentifier() {
			return ID;
		}

		@Override
		public BlockTagIngredient read(JsonObject json) {
			ResourceLocation rl = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
			TagKey<Block> tag = TagKey.create(Registries.BLOCK, rl);
			return new BlockTagIngredient(tag);
		}

		@Override
		public void write(JsonObject json, BlockTagIngredient ingredient) {
			json.addProperty("tag", ingredient.tag.location().toString());
		}

		@Override
		public BlockTagIngredient read(FriendlyByteBuf buffer) {
			ResourceLocation rl = buffer.readResourceLocation();
			TagKey<Block> tag = TagKey.create(Registries.BLOCK, rl);
			return new BlockTagIngredient(tag);
		}

		@Override
		public void write(FriendlyByteBuf buf, BlockTagIngredient ingredient) {
			buf.writeResourceLocation(ingredient.tag.location());
		}
	}
}
