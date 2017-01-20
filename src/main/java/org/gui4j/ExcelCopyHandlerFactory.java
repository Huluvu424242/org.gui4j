package org.gui4j;


public interface ExcelCopyHandlerFactory
{
    /**
     * @param guiId the guiId of the table in the xml file
     * @return ExcelCopyHandler to copy the table content to the clipboard
     */
    ExcelCopyHandler createExcelCopyHandler(String guiId);
}
