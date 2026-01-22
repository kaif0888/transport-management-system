import { useState } from "react";
import { Upload, Table as AntTable } from "antd";
import { InboxOutlined } from "@ant-design/icons";
import { Button } from "@/Components/atom/Button";
import { uploadVehicleFile } from "@/service/vehicle";
import { toast } from "react-toastify";
import * as XLSX from "xlsx";
import Papa from "papaparse";

const { Dragger } = Upload;


const ImportVehicle = ({ handleCancel, vehicleListDataRefetch }) => {
  const [fileList, setFileList] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [parsedData, setParsedData] = useState([]);
  const [columns, setColumns] = useState([]);

  const handleUpload = async () => {
    if (fileList.length === 0) {
      toast.error("Please select a file to upload.");
      return;
    }
    const formData = new FormData();
    fileList.forEach((file) => {
      formData.append("file", file);
    });

    setUploading(true);

    try {
      await uploadVehicleFile(formData);
      setFileList([]);
      setParsedData([]);
      setColumns([]);
      toast.success("File uploaded successfully.");
      vehicleListDataRefetch();
      handleCancel();
    } catch (error) {
      console.error("Upload error:", error);
      toast.error(error?.response?.data?.message || "File upload failed.");
    } finally {
      setUploading(false);
    }
  };

  const props = {
    onRemove: (file) => {
      const index = fileList.indexOf(file);
      const newFileList = fileList.slice();
      newFileList.splice(index, 1);
      setFileList(newFileList);
      setParsedData([]);
      setColumns([]);
    },
    beforeUpload: (file) => {
      const isCSV = file.type === "text/csv" || file.name.endsWith(".csv");
      const isExcel =
        file.type === "application/vnd.ms-excel" ||
        file.type === "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ||
        file.name.endsWith(".xls") ||
        file.name.endsWith(".xlsx");

      if (!isCSV && !isExcel) {
        toast.error(`${file.name} is not a CSV or Excel file`);
        return Upload.LIST_IGNORE;
      }

      const reader = new FileReader();
      
      reader.onload = (e) => {
        try {
          if (isCSV) {
            // For CSV files, read as text
            Papa.parse(e.target.result, {
              header: true,
              skipEmptyLines: true,
              dynamicTyping: true,
              complete: (results) => {
                if (results.data && results.data.length > 0) {
                  // Clean up headers by trimming whitespace
                  const cleanedData = results.data.map(row => {
                    const cleanRow = {};
                    Object.keys(row).forEach(key => {
                      const cleanKey = key.trim();
                      cleanRow[cleanKey] = row[key];
                    });
                    return cleanRow;
                  });

                  const firstRow = cleanedData[0];
                  const tableColumns = Object.keys(firstRow)
                    .filter(key => key) // Filter out empty keys
                    .map((key) => ({
                      title: key,
                      dataIndex: key,
                      key: key,
                      render: (text) => (text !== null && text !== undefined ? String(text) : '-')
                    }));
                  
                  setColumns(tableColumns);
                  setParsedData(cleanedData.map((row, index) => ({ ...row, key: index })));
                  setFileList([file]);
                  toast.success(`CSV file parsed successfully. Found ${cleanedData.length} rows.`);
                } else {
                  toast.error("CSV file is empty or invalid.");
                }
              },
              error: (error) => {
                console.error("CSV parsing error:", error);
                toast.error(`Error parsing CSV file: ${error.message}`);
              },
            });
          } else {
            // For Excel files, read as ArrayBuffer
            const data = new Uint8Array(e.target.result);
            const workbook = XLSX.read(data, { type: "array" });
            const sheetName = workbook.SheetNames[0];
            const worksheet = workbook.Sheets[sheetName];
            
            // Convert to JSON with proper options
            const jsonData = XLSX.utils.sheet_to_json(worksheet, {
              raw: false, // Convert dates and numbers to strings
              defval: null // Set default value for empty cells
            });

            if (jsonData && jsonData.length > 0) {
              // Clean up headers by trimming whitespace
              const cleanedData = jsonData.map(row => {
                const cleanRow = {};
                Object.keys(row).forEach(key => {
                  const cleanKey = key.trim();
                  cleanRow[cleanKey] = row[key];
                });
                return cleanRow;
              });

              const headers = Object.keys(cleanedData[0]).filter(key => key);
              const tableColumns = headers.map((header) => ({
                title: header,
                dataIndex: header,
                key: header,
                render: (text) => (text !== null && text !== undefined ? String(text) : '-')
              }));
              
              setColumns(tableColumns);
              setParsedData(cleanedData.map((row, index) => ({ ...row, key: index })));
              setFileList([file]);
              toast.success(`Excel file parsed successfully. Found ${cleanedData.length} rows.`);
            } else {
              toast.error("Excel file is empty or has no data.");
            }
          }
        } catch (error) {
          console.error("File reading error:", error);
          toast.error(`An error occurred while reading the file: ${error.message}`);
        }
      };

      reader.onerror = (error) => {
        console.error("FileReader error:", error);
        toast.error("Failed to read the file. Please try again.");
      };

      // Use appropriate read method based on file type
      if (isCSV) {
        reader.readAsText(file, "UTF-8");
      } else {
        reader.readAsArrayBuffer(file);
      }

      return false; // Prevent auto-upload
    },
    fileList,
    maxCount: 1,
  };

  return (
    <>
      <Dragger {...props}>
        <p className="ant-upload-drag-icon">
          <InboxOutlined />
        </p>
        <p className="ant-upload-text">
          Click or drag CSV or Excel file to this area to upload
        </p>
        <p className="ant-upload-hint">
          Support for a single CSV/Excel file upload. The file will be validated before upload.
        </p>
      </Dragger>
      
      {parsedData.length > 0 && (
        <div style={{ marginTop: 16 }}>
          <h3 style={{ marginBottom: 8 }}>Preview ({parsedData.length} rows)</h3>
          <AntTable
            columns={columns}
            dataSource={parsedData}
            pagination={{ pageSize: 5 }}
            scroll={{ y: 240, x: 'max-content' }}
            size="small"
          />
        </div>
      )}
      
      <Button
        type="primary"
        onClick={handleUpload}
        disabled={fileList.length === 0}
        loading={uploading}
        style={{ marginTop: 16 }}
        className="bg-[#750014]"
      >
        {uploading ? "Uploading" : "Start Upload"}
      </Button>
    </>
  );
};

export default ImportVehicle;