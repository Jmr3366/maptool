/*
 * This software Copyright by the RPTools.net development team, and
 * licensed under the Affero GPL Version 3 or, at your option, any later
 * version.
 *
 * MapTool Source Code is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU Affero General Public
 * License * along with this source Code.  If not, please visit
 * <http://www.gnu.org/licenses/> and specifically the Affero license
 * text at <http://www.gnu.org/licenses/agpl.html>.
 */
package net.rptools.maptool.model.framework.dropinlibrary;

import com.google.protobuf.util.JsonFormat;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.filechooser.FileFilter;
import net.rptools.lib.MD5Key;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.Asset.Type;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.framework.proto.DropInLibraryDto;
import org.apache.tika.mime.MediaType;
import org.javatuples.Pair;

/**
 * Class for importing Drop In Libraries.
 */
public class DropInLibraryImporter {

  /** The file extension for drop in library files. */
  public static final String DROP_IN_LIBRARY_EXTENSION = ".mtlib";
  /** The name of the drop in library config file. */
  private static final String LIBRARY_INFO_FILE = "library.json";
  /** the directory where all the content files in the library live. */
  private static final String CONTENT_DIRECTORY = "content/";

  /**
   * Returns the {@link FileFilter} for drop in library files.
   * @return the {@link FileFilter} for drop in library files.
   */
  public static FileFilter getDropInLibraryFileFilter() {
    return new FileFilter() {

      @Override
      public boolean accept(File f) {
        return f.isDirectory() || f.getName().endsWith(DROP_IN_LIBRARY_EXTENSION);
      }

      @Override
      public String getDescription() {
        return I18N.getText("file.ext.dropInLib");
      }
    };
  }

  /**
   * Imports the drop in library from the specified file.
   * @param file the file to use for import.
   * @return
   *
   * @throws IOException
   */
  public DropInLibrary importFromFile(File file) throws IOException {
    var diiBuilder = DropInLibraryDto.newBuilder();
    try (var zip = new ZipFile(file)) {
      ZipEntry entry = zip.getEntry(LIBRARY_INFO_FILE);
      if (entry == null) {
        throw new IOException("library.json file not found.");
      }
      var builder = DropInLibraryDto.newBuilder();
      JsonFormat.parser().merge(new InputStreamReader(zip.getInputStream(entry)), builder);
      var pathAssetMap = transferAssets(builder.getNamespace(), zip);
      //return DropInLibrary.fromDto(builder.build(), pathAssetMap);
      return DropInLibrary.fromDto(builder.build(), Map.of());
    }
  }

  private Map<String, Pair<MD5Key, Type>> transferAssets(String namespace, ZipFile zip) throws IOException {
    var pathAssetMap = new HashMap<String, Pair<MD5Key, Type>>();
    var entries =
        zip.stream()
            .filter(e -> !e.isDirectory())
            .filter(e -> e.getName().startsWith(CONTENT_DIRECTORY))
            .toList();
    for (var entry : entries) {
      try (InputStream inputStream = zip.getInputStream(entry)) {
        byte[] bytes = inputStream.readAllBytes();
        MediaType mediaType = Asset.getMediaType(entry.getName(), bytes);
        Asset asset =
            Type.fromMediaType(mediaType).getFactory().apply(namespace + "/" + entry.getName(),
            bytes);
        if (!AssetManager.hasAsset(asset)) {
          AssetManager.putAsset(asset);
        }
        pathAssetMap.put(entry.getName(), Pair.with(asset.getMD5Key(), asset.getType()));
      }
    }
    return pathAssetMap;
  }
}
