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
package net.rptools.maptool.model.library.addon;

import java.nio.file.Path;
import net.rptools.maptool.model.library.LibraryInfo;

/**
 * Represents the information about external add-on library.
 *
 * @param namespace The namespace of the add-on.
 * @param libraryInfo The library info of the add-on.
 * @param updatedOnDisk Whether the add-on has been updated on disk.
 * @param isInstalled Whether the add-on is installed.
 * @param backingDirectory The backing directory of the add-on.
 * @param subDirectoryName The subdirectory name of the add-on in the add-on development dir.
 */
public record ExternalLibraryInfo(
    String namespace,
    LibraryInfo libraryInfo,
    boolean updatedOnDisk,
    boolean isInstalled,
    Path backingDirectory,
    String subDirectoryName) {}
