package shadows.hostilenetworks.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import shadows.hostilenetworks.Hostile;
import shadows.hostilenetworks.HostileNetworks;
import shadows.hostilenetworks.data.DataModel;
import shadows.hostilenetworks.data.DataModelManager;
import shadows.hostilenetworks.item.DataModelItem;

@JeiPlugin
public class HostileJeiPlugin implements IModPlugin {

	@Override
	public void registerItemSubtypes(ISubtypeRegistration reg) {
		reg.registerSubtypeInterpreter(Hostile.Items.DATA_MODEL, new ModelSubtypes());
		reg.registerSubtypeInterpreter(Hostile.Items.PREDICTION, new ModelSubtypes());
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration reg) {
		reg.addRecipeCategories(new SimChamberCategory(reg.getJeiHelpers().getGuiHelper()));
		reg.addRecipeCategories(new LootFabCategory(reg.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration reg) {
		SimChamberCategory.recipes = DataModelManager.INSTANCE.getAllModels().stream().map(TickingDataModelWrapper::new).collect(Collectors.toList());
		reg.addRecipes(SimChamberCategory.recipes, SimChamberCategory.UID);
		List<LootFabRecipe> fabRecipes = new ArrayList<>();
		for (DataModel dm : DataModelManager.INSTANCE.getAllModels()) {
			for (int i = 0; i < dm.getFabDrops().size(); i++) {
				fabRecipes.add(new LootFabRecipe(dm, i));
			}
		}
		reg.addRecipes(fabRecipes, LootFabCategory.UID);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
		reg.addRecipeCatalyst(new ItemStack(Hostile.Blocks.SIM_CHAMBER), SimChamberCategory.UID);
		reg.addRecipeCatalyst(new ItemStack(Hostile.Blocks.LOOT_FABRICATOR), LootFabCategory.UID);
	}

	private class ModelSubtypes implements IIngredientSubtypeInterpreter<ItemStack> {

		@Override
		public String apply(ItemStack stack, UidContext context) {
			DataModel dm = DataModelItem.getStoredModel(stack);
			if (dm == null) return "NULL";
			return dm.getId().toString();
		}

	}

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(HostileNetworks.MODID, "plugin");
	}

}
