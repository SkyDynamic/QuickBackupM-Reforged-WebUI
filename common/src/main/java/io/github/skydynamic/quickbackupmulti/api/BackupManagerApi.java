package io.github.skydynamic.quickbackupmulti.api;

import io.github.skydynamic.increment.storage.lib.database.StorageInfo;
import io.github.skydynamic.quickbackupmulti.response.ApiResponse;
import io.github.skydynamic.quickbackupmulti.response.ApiStatusCode;
import io.github.skydynamic.quickbackupmulti.security.RequireLogin;
import io.github.skydynamic.quickbakcupmulti.QuickbakcupmultiReforged;
import io.github.skydynamic.quickbakcupmulti.restore.RestoreTimer;
import io.github.skydynamic.quickbakcupmulti.utils.BackupManager;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

@Path("api/backups")
public class BackupManagerApi {
    @GET
    @Path("get")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResponse<StorageInfo> getBackup(@QueryParam("name") String name) {
        try {
            StorageInfo backup = QuickbakcupmultiReforged.getDatabase()
                .getStorageInfoWithName(name);
            if (backup == null) {
                return new ApiResponse<>(ApiStatusCode.BACKUP_NOT_FOUND, null);
            } else {
                return new ApiResponse<>(backup);
            }
        } catch (Exception e) {
            return new ApiResponse<>(ApiStatusCode.SERVER_NOT_STARTED, null);
        }
    }

    @GET
    @Path("getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResponse<List<StorageInfo>> getBackups() {
        try {
            List<StorageInfo> backups = QuickbakcupmultiReforged.getDatabase()
                .getAllStorageInfo();
            return new ApiResponse<>(backups);
        } catch (Exception e) {
            return new ApiResponse<>(ApiStatusCode.SERVER_NOT_STARTED, new ArrayList<>());
        }
    }

    @RequireLogin
    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResponse<Void> deleteBackup(@FormParam("name") String name) {
        try {
            boolean result = BackupManager.deleteBackup(null, name);
            if (!result) {
                return new ApiResponse<>(ApiStatusCode.BACKUP_NOT_FOUND, null);
            } else {
                return new ApiResponse<>(null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(ApiStatusCode.SERVER_NOT_STARTED, null);
        }
    }

    @RequireLogin
    @POST
    @Path("create")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResponse<String> createBackup(
        @FormParam("name") String name,
        @FormParam("desc") String desc
    ) {
        if (name == null || name.isEmpty()) {
            return new ApiResponse<>(ApiStatusCode.BACKUP_NAME_IS_NULL, "");
        }
        if (desc == null) {
            desc = "";
        }

        if (QuickbakcupmultiReforged.getDatabase().storageExists(name)) {
            return new ApiResponse<>(ApiStatusCode.BACKUP_EXISTS, "");
        }
        try {
            BackupManager.makeBackup(
                QuickbakcupmultiReforged.getServerManager().getCommandSource(),
                name,
                desc
            );
            return new ApiResponse<>("Make Backup Success");
        } catch (Exception e) {
            return new ApiResponse<>(ApiStatusCode.SERVER_NOT_STARTED, "");
        }
    }

    @RequireLogin
    @POST
    @Path("restore")
    @Produces(MediaType.APPLICATION_JSON)
    public ApiResponse<String> restoreBackup(@FormParam("name") String name) {
        try {
           boolean backupExists = QuickbakcupmultiReforged.getDatabase().storageExists(name);
            if (!backupExists) {
                return new ApiResponse<>(ApiStatusCode.BACKUP_NOT_FOUND, "");
            } else {
                QuickbakcupmultiReforged.getModContainer().setCurrentSelectionBackup(name);
                List<ServerPlayer> players = new ArrayList<>(QuickbakcupmultiReforged.getServerManager().getPlayers());
                new RestoreTimer(QuickbakcupmultiReforged.getModContainer().getEnvType(), players).run();
                return new ApiResponse<>("Restore Backup Success");
            }
        } catch (Exception e) {
            return new ApiResponse<>(ApiStatusCode.SERVER_NOT_STARTED, "");
        }
    }
}
