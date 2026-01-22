import React, {
  useMemo,
  forwardRef,
  useImperativeHandle,
  useState,
} from "react";
import { useQuery } from "@tanstack/react-query";
import { Table as AntTable, Spin } from "antd";
import Papa from "papaparse";
import { Button } from "./Button";

export const Table = forwardRef(function Table(
  {
    dependentLoader = false,
    query,
    formatData = (data) => data,
    notFoundComponent,
    columns,
    handleRefetch,
    exportEnabled = false,
    extraHeaderContent,
    scrollx = 800, // Use scrollx instead of scroll object
    ...rest
  },
  ref
) {
  const { isFetching, isFetched, data = [], refetch } = useQuery({
    ...query,
  });

  const [searchText, setSearchText] = useState({});

  function Refetch() {
    refetch();
  }

  useImperativeHandle(ref, () => ({
    Refetch,
    data,
  }));

  const filteredDataSource = useMemo(() => {
    let formattedData = formatData(data).map((dt, idx) => ({
      ...dt,
      idx: idx + 1,
    }));

    if (Object.keys(searchText).length > 0) {
      return formattedData.filter((record) => {
        return Object.keys(searchText).every((key) => {
          if (!searchText[key]) return true;
          const recordValue = record[key]?.toString().toLowerCase();
          return recordValue && recordValue.includes(searchText[key].toLowerCase());
        });
      });
    }

    return formattedData;
  }, [data, searchText]);

  const isLoading = isFetching && !data.length;
  const showLoader = isLoading || dependentLoader;

  if (isFetched && data.length === 0 && !dependentLoader && notFoundComponent) {
    return notFoundComponent;
  }

  const enhancedColumns = useMemo(() => {
    return columns.map((col) => ({
      ...col,
      title: <div><div>{col.title}</div></div>,
      filterDropdown: false,
      filterIcon: false,
      onHeaderCell: () => ({
        style: {
          backgroundColor: "#a0051f",
          color: "white",
        },
      }),
    }));
  }, [columns]);

  // 🔽 CSV Export Function
  function exportToCSV() {
    if (!filteredDataSource.length) return;

    const csv = Papa.unparse(filteredDataSource);
    const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", "export.csv");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  // Create scroll configuration
  const scrollConfig = useMemo(() => {
    return {
      x: scrollx || 800,
      y: undefined // Let table auto-adjust height
    };
  }, [scrollx]);

  return (
    <div>
      {/* Top Row: Export + Extra Buttons */}
      {(exportEnabled || extraHeaderContent) && (
        <div className="flex items-center mb-4 gap-2">
          {extraHeaderContent && <div className="flex w-full gap-2">{extraHeaderContent}</div>}
          {exportEnabled && (
            <Button type="primary" onClick={exportToCSV}>
              Export CSV
            </Button>
          )}
        </div>
      )}

      {/* Ant Design Table - Remove the outer overflow-x-auto div */}
      <AntTable
        loading={{
          spinning: showLoader,
          indicator: (
            <div className="flex items-center justify-center -translate-x-[18px] translate-y-6">
              <Spin size="large" />
            </div>
          ),
        }}
        dataSource={filteredDataSource}
        columns={enhancedColumns}
        onHeaderRow={() => ({
          style: { backgroundColor: "#a0051f", color: "white" },
        })}
        scroll={scrollConfig}
        {...rest}
      />
    </div>
  );
});