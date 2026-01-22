import React, { useState, useCallback, useMemo } from "react";
import { Form, Upload, Progress, message } from "antd";
import { 
  PlusOutlined, 
  FileOutlined, 
  FilePdfOutlined, 
  FileWordOutlined, 
  FileImageOutlined, 
  CloseOutlined,
  DownloadOutlined,
  EyeOutlined,
  ExclamationCircleOutlined,
  LoadingOutlined
} from "@ant-design/icons";

const cn = (...classes) => classes.filter(Boolean).join(' ');

const SUPPORTED_IMAGE_FORMATS = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'jfif', 'bmp', 'svg'];
const PROGRESS_INTERVAL = 150;
const PROGRESS_INCREMENT = 10;

const getFileExtension = (fileName) => {
  return fileName?.toLowerCase().split('.').pop() || '';
};

const isImageFile = (fileName) => {
  const extension = getFileExtension(fileName);
  return SUPPORTED_IMAGE_FORMATS.includes(extension);
};

const isLocalFile = (file) => {
  const url = file.url || file.fileUrl;
  if (!url) return false;
  return url.includes(':\\') || (url.startsWith('/') && !url.startsWith('//') && !url.startsWith('http'));
};

const convertLocalPathToUrl = (localPath) => {
  if (!localPath || !isLocalFile({ url: localPath })) return null;
  
  try {
    return `/api/files/serve?path=${encodeURIComponent(localPath)}`;
  } catch (e) {
    return null;
  }
};

const FileIcon = ({ fileName }) => {
  const extension = getFileExtension(fileName);
  const iconProps = { className: "text-3xl mb-3" };
  
  switch (extension) {
    case 'pdf':
      return <FilePdfOutlined {...iconProps} className={`${iconProps.className} text-red-500`} />;
    case 'doc':
    case 'docx':
      return <FileWordOutlined {...iconProps} className={`${iconProps.className} text-blue-500`} />;
    case 'jpg':
    case 'jpeg':
    case 'png':
    case 'gif':
    case 'webp':
    case 'jfif':
    case 'bmp':
    case 'svg':
      return <FileImageOutlined {...iconProps} className={`${iconProps.className} text-green-500`} />;
    default:
      return <FileOutlined {...iconProps} className={`${iconProps.className} text-gray-500`} />;
  }
};

const FileStatusIndicator = ({ file, loadingStates, imageErrors, enableLocalFileAccess }) => {
  const loadingState = loadingStates.get(file.uid);
  
  if (loadingState === 'loading') {
    return (
      <div className="flex items-center justify-center w-4 h-4 bg-blue-500 rounded-full absolute -top-1 -left-1">
        <LoadingOutlined className="text-white text-xs animate-spin" />
      </div>
    );
  }
  
  if (isLocalFile(file) && !enableLocalFileAccess) {
    return (
      <div className="flex items-center justify-center w-4 h-4 bg-orange-500 rounded-full absolute -top-1 -left-1">
        <ExclamationCircleOutlined className="text-white text-xs" />
      </div>
    );
  }
  
  if (imageErrors.has(file.uid) || loadingState === 'error') {
    return (
      <div className="flex items-center justify-center w-4 h-4 bg-red-500 rounded-full absolute -top-1 -left-1">
        <ExclamationCircleOutlined className="text-white text-xs" />
      </div>
    );
  }
  
  return null;
};

const ActionButtons = ({ 
  file, 
  showDownload, 
  onDownload, 
  onPreview, 
  onRemove, 
  showRemove,
  imageErrors,
  enableLocalFileAccess 
}) => {
  const fileName = file.name || file.fileName || 'Unknown file';
  const isExistingFile = (file.status === 'done' && (file.url || file.fileUrl)) || file.fileUrl;
  const hasImageError = imageErrors.has(file.uid);
  const isAccessible = enableLocalFileAccess || !isLocalFile(file);
  const isLocal = isLocalFile(file);
  
  return (
    <div className="absolute -top-2 -right-2 flex gap-1">
      {showDownload && (isExistingFile || file.originFileObj) && (
        <button
          type="button"
          onClick={(e) => onDownload(file, e)}
          className="w-6 h-6 bg-blue-500 text-white rounded-full flex items-center justify-center hover:bg-blue-600 transition-colors duration-200 cursor-pointer shadow-md z-10"
          title={isLocal ? "Download via Next.js API" : "Download file"}
        >
          <DownloadOutlined className="text-xs" />
        </button>
      )}
      {onPreview &&
       (isExistingFile || file.originFileObj) && 
       !hasImageError && 
       isAccessible && (
        <button
          type="button"
          onClick={(e) => onPreview(file, e)}
          className="w-6 h-6 bg-green-500 text-white rounded-full flex items-center justify-center hover:bg-green-600 transition-colors duration-200 cursor-pointer shadow-md z-10"
          title="Preview image"
        >
          <EyeOutlined className="text-xs" />
        </button>
      )}
      {showRemove && (
        <button
          type="button"
          onClick={(e) => onRemove(file, e)}
          className="w-6 h-6 bg-red-500 text-white rounded-full flex items-center justify-center hover:bg-red-600 transition-colors duration-200 cursor-pointer shadow-md z-10"
          title="Remove file"
        >
          <CloseOutlined className="text-xs" />
        </button>
      )}
    </div>
  );
};

export const FileUpload = ({
  required,
  label,
  labelProps,
  formClassName,
  validateStatus,
  help,
  error,
  touched,
  id,
  multiple = false,
  maxCount,
  fileList = [],
  acceptedTypes = [".pdf", ".jpeg", ".jpg", ".png", ".doc", ".docx"],
  onChange,
  showDownload = false,
  onDownload,
  onPreview,
  enableLocalFileAccess = true,
  maxFileSize = 10 * 1024 * 1024,
  ...rest
}) => {
  const [isUploading, setIsUploading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewImage, setPreviewImage] = useState('');
  const [imageErrors, setImageErrors] = useState(new Set());
  const [loadingStates, setLoadingStates] = useState(new Map());
  const [blobUrls, setBlobUrls] = useState(new Map());
  
  const showError = error && touched;

  const validateFile = useCallback((file) => {
    if (file.size > maxFileSize) {
      message.error(`File size must be less than ${Math.round(maxFileSize / 1024 / 1024)}MB`);
      return false;
    }
    
    const fileExtension = `.${getFileExtension(file.name)}`;
    if (!acceptedTypes.includes(fileExtension)) {
      message.error(`File type not supported. Accepted types: ${acceptedTypes.join(', ')}`);
      return false;
    }
    
    return true;
  }, [maxFileSize, acceptedTypes]);

  const getImageUrl = useCallback((file) => {
    if (blobUrls.has(file.uid)) {
      return blobUrls.get(file.uid);
    }
    
    if (file.originFileObj) {
      const blobUrl = URL.createObjectURL(file.originFileObj);
      setBlobUrls(prev => new Map(prev).set(file.uid, blobUrl));
      return blobUrl;
    }
    
    const url = file.url || file.fileUrl;
    if (!url) return null;
    
    if (isLocalFile(file)) {
      return enableLocalFileAccess ? convertLocalPathToUrl(url) : null;
    }
    
    return url;
  }, [blobUrls, enableLocalFileAccess]);

  const handleFileChange = useCallback((info) => {
    if (info.file.originFileObj && !validateFile(info.file.originFileObj)) {
      return;
    }
    
    onChange?.(info);

    const hasNewFiles = info.fileList.length > (fileList?.length || 0);
    
    if (hasNewFiles) {
      setIsUploading(true);
      setProgress(0);
      
      const interval = setInterval(() => {
        setProgress(prev => {
          if (prev >= 100) {
            clearInterval(interval);
            setIsUploading(false);
            return 100;
          }
          return prev + PROGRESS_INCREMENT;
        });
      }, PROGRESS_INTERVAL);
    }
  }, [validateFile, onChange, fileList]);

  const handleRemoveFile = useCallback((fileToRemove, event) => {
    event.stopPropagation();
    
    const blobUrl = blobUrls.get(fileToRemove.uid);
    if (blobUrl?.startsWith('blob:')) {
      URL.revokeObjectURL(blobUrl);
    }
    
    setBlobUrls(prev => {
      const newMap = new Map(prev);
      newMap.delete(fileToRemove.uid);
      return newMap;
    });
    
    setImageErrors(prev => {
      const newSet = new Set(prev);
      newSet.delete(fileToRemove.uid);
      return newSet;
    });
    
    setLoadingStates(prev => {
      const newMap = new Map(prev);
      newMap.delete(fileToRemove.uid);
      return newMap;
    });
    
    const newFileList = fileList.filter(file => file.uid !== fileToRemove.uid);
    
    onChange?.({
      file: fileToRemove,
      fileList: newFileList
    });
  }, [fileList, onChange, blobUrls]);

  const handleDownload = useCallback((file, event) => {
    event.stopPropagation();
    
    if (onDownload) {
      onDownload(file);
      return;
    }
    
    const downloadUrl = getImageUrl(file);
    if (!downloadUrl) {
      message.error('Unable to download file. File may not be accessible.');
      return;
    }
    
    if (isLocalFile(file)) {
      window.open(downloadUrl, '_blank');
      return;
    }
    
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = file.name || file.fileName || 'download';
    link.target = '_blank';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }, [onDownload, getImageUrl]);

  const handlePreview = useCallback((file, event) => {
    event.stopPropagation();
    if (onPreview) {
      onPreview(file, event);
      return;
    }
    const fileName = file.name || file.fileName;
    
    if (isImageFile(fileName)) {
      const imageUrl = getImageUrl(file);
      if (imageUrl) {
        setPreviewImage(imageUrl);
        setPreviewVisible(true);
      } else {
        message.error('Unable to preview image. File may not be accessible.');
      }
    } else {
      handleDownload(file, event);
    }
  }, [onPreview, getImageUrl, handleDownload]);

  const handleImageError = useCallback((file) => {
    setImageErrors(prev => new Set([...prev, file.uid]));
    setLoadingStates(prev => new Map(prev).set(file.uid, 'error'));
  }, []);

  const handleImageLoad = useCallback((file) => {
    setLoadingStates(prev => new Map(prev).set(file.uid, 'loaded'));
  }, []);

  const handleImageLoadStart = useCallback((file) => {
    setLoadingStates(prev => new Map(prev).set(file.uid, 'loading'));
  }, []);

  const isFileAccessible = useCallback((file) => {
    if (file.originFileObj || blobUrls.has(file.uid)) {
      return true;
    }
    
    const url = file.url || file.fileUrl;
    if (!url) return false;
    
    if (isLocalFile(file)) {
      return enableLocalFileAccess;
    }
    
    return true;
  }, [blobUrls, enableLocalFileAccess]);

  const getFilePreview = useCallback((file, showRemoveButton = false) => {
    const fileName = file.name || file.fileName || 'Unknown file';
    const hasImageError = imageErrors.has(file.uid);
    const isAccessible = isFileAccessible(file);
    const isLocal = isLocalFile(file);
    const loadingState = loadingStates.get(file.uid);
    
    const actionButtons = (
      <ActionButtons
        file={file}
        showDownload={showDownload}
        onDownload={handleDownload}
        onPreview={handlePreview}
        onRemove={handleRemoveFile}
        showRemove={showRemoveButton}
        imageErrors={imageErrors}
        enableLocalFileAccess={enableLocalFileAccess}
      />
    );
    
    if (isImageFile(fileName) && !hasImageError && isAccessible) {
      const imageUrl = getImageUrl(file);
      if (imageUrl) {
        return (
          <div className="relative">
            <div className="w-60 h-30 mb-1 rounded-md overflow-hidden border-2 border-gray-200 shadow-sm relative">
              {loadingState === 'loading' && (
                <div className="absolute inset-0 flex items-center justify-center bg-gray-50">
                  <LoadingOutlined className="text-gray-400 text-xl animate-spin" />
                </div>
              )}
              <img 
                src={imageUrl} 
                alt={fileName}
                className="w-full h-full object-cover"
                onError={() => handleImageError(file)}
                onLoad={() => handleImageLoad(file)}
                onLoadStart={() => handleImageLoadStart(file)}
                loading="lazy"
                style={{ display: loadingState === 'loading' ? 'none' : 'block' }}
              />
            </div>
            <FileStatusIndicator
              file={file}
              loadingStates={loadingStates}
              imageErrors={imageErrors}
              enableLocalFileAccess={enableLocalFileAccess}
            />
            {actionButtons}
          </div>
        );
      }
    }
    
    return (
      <div className="relative">
        <div className="flex flex-col items-center">
          <div className="relative">
            <FileIcon fileName={fileName} />
            <FileStatusIndicator
              file={file}
              loadingStates={loadingStates}
              imageErrors={imageErrors}
              enableLocalFileAccess={enableLocalFileAccess}
            />
          </div>
          {isLocal && (
            <div className={cn(
              "text-xs mt-1 text-center max-w-20",
              enableLocalFileAccess ? "text-blue-500" : "text-orange-500"
            )}>
              Local file
            </div>
          )}
          {hasImageError && (
            <div className="text-xs text-red-500 mt-1 text-center max-w-20">
              Load error
            </div>
          )}
        </div>
        {actionButtons}
      </div>
    );
  }, [
    imageErrors,
    loadingStates,
    isFileAccessible,
    showDownload,
    handleDownload,
    handlePreview,
    handleRemoveFile,
    enableLocalFileAccess,
    getImageUrl,
    handleImageError,
    handleImageLoad,
    handleImageLoadStart
  ]);

  const displayContent = useMemo(() => {
    if (isUploading) {
      return (
        <div className="w-full">
          <div className="text-sm font-medium mb-3 text-gray-700 text-center">
            Processing file...
          </div>
          <Progress 
            percent={progress} 
            strokeColor="#22c55e"
            trailColor="#e5e7eb"
            showInfo={false}
            size="small"
          />
        </div>
      );
    }

    if (fileList?.length > 0) {
      if (multiple) {
        return (
          <div className="flex flex-col items-center">
            <div className="flex flex-wrap gap-4 mb-4 justify-center">
              {fileList.slice(0, 3).map((file, index) => (
                <div key={file.uid || index} className="flex flex-col items-center">
                  {getFilePreview(file, true)}
                  <div className="text-xs text-gray-600 max-w-20 truncate text-center">
                    {file.name || file.fileName || 'File'}
                  </div>
                </div>
              ))}
              {fileList.length > 3 && (
                <div className="flex flex-col items-center">
                  <div className="flex items-center justify-center w-20 h-20 bg-gray-100 rounded-lg border-2 border-gray-200 mb-3">
                    <span className="text-sm font-medium text-gray-600">+{fileList.length - 3}</span>
                  </div>
                  <div className="text-xs text-gray-600">more files</div>
                </div>
              )}
            </div>
            <div className="text-sm font-medium mb-2 text-center text-gray-700">
              {fileList.length} file{fileList.length > 1 ? 's' : ''} selected
            </div>
            <div className="text-xs text-gray-500 text-center">
              Click to add more files
            </div>
          </div>
        );
      } else {
        const file = fileList[0];
        return (
          <div className="flex flex-col items-center">
            <div className="mb-2">
              {getFilePreview(file, true)}
            </div>
            <div className="text-sm font-medium mb-2 text-center text-gray-700 max-w-64 truncate">
              {file.name || file.fileName || 'File selected'}
            </div>
            <div className="text-xs text-gray-500 text-center">
              Click to change file
            </div>
          </div>
        );
      }
    }

    return (
      <div className="flex flex-col items-center">
        <PlusOutlined className="text-3xl mb-3 text-gray-400" />
        <div className="text-sm font-medium mb-2 text-center text-gray-700">
          {multiple ? "Upload Files" : "Upload File"}
        </div>
        <div className="text-xs text-gray-500 text-center">
          Supported: {acceptedTypes.join(", ")}
        </div>
        <div className="text-xs text-gray-400 text-center mt-1">
          Max size: {Math.round(maxFileSize / 1024 / 1024)}MB
        </div>
      </div>
    );
  }, [isUploading, progress, fileList, multiple, getFilePreview, acceptedTypes, maxFileSize]);

  const uploadProps = useMemo(() => ({
    ...rest,
    multiple,
    fileList,
    onChange: handleFileChange,
    beforeUpload: () => false,
    ...(multiple && maxCount && { maxCount }),
    ...(!multiple && { maxCount: 1 }),
  }), [rest, multiple, fileList, handleFileChange, maxCount]);

  return (
    <div className="w-full mb-2">
      {label && (
        <label
          htmlFor={id}
          {...labelProps}
          className={cn(
            "text-sm font-semibold text-gray-700 mb-1 flex items-center",
            labelProps?.className
          )}
        >
          {label}
          {required && <span className="text-red-600 ml-1 text-sm">*</span>}
        </label>
      )}
      
      <div className={cn(
        "border border-gray-200 p-2 max-w-max rounded-lg hover:border-red-600 transition-all duration-200",
        showError && "border-red-600"
      )}>
        <Form.Item
          className={formClassName}
          validateStatus={showError ? "error" : validateStatus}
          help={showError ? error : help}
          valuePropName="fileList"
          style={{ marginBottom: 0 }}
        >
          <Upload
            {...uploadProps}
            className="file-upload-container w-full"
            showUploadList={false}
          >
            <div
              className={cn(
                "border border-dashed rounded-lg p-6 min-w-80 hover:border-red-600 transition-all duration-200",
                "flex flex-col items-center justify-center min-h-[140px]",
                isUploading ? "cursor-not-allowed" : "cursor-pointer",
                showError
                  ? "border-red-600 bg-red-50"
                  : "border-gray-300 hover:bg-gray-50"
              )}
            >
              {displayContent}
            </div>
          </Upload>
        </Form.Item>
      </div>

      {showError && (
        <div className="text-red-600 text-xs mt-2">{error}</div>
      )}

      {fileList?.some(file => isLocalFile(file)) && (
        <div className={cn(
          "text-xs mt-2 p-2 rounded border",
          enableLocalFileAccess 
            ? "text-blue-600 bg-blue-50 border-blue-200"
            : "text-orange-600 bg-orange-50 border-orange-200"
        )}>
          <ExclamationCircleOutlined className="mr-1" />
          {enableLocalFileAccess 
            ? "Local files are being served through the local file server."
            : "Local files cannot be displayed. Enable local file access to view them."
          }
        </div>
      )}

      {previewVisible && (
        <div 
          className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
          onClick={() => setPreviewVisible(false)}
        >
          <div className="relative max-w-4xl max-h-4xl p-4">
            <img 
              src={previewImage} 
              alt="Preview" 
              className="max-w-full max-h-full object-contain"
            />
            <button
              onClick={() => setPreviewVisible(false)}
              className="absolute top-2 right-2 w-8 h-8 bg-white rounded-full flex items-center justify-center hover:bg-gray-100 transition-colors duration-200"
            >
              <CloseOutlined className="text-gray-600" />
            </button>
          </div>
        </div>
      )}

      <style jsx>{`
        .file-upload-container .ant-upload-select {
          border: none !important;
          background: transparent !important;
          width: 100% !important;
        }
        .file-upload-container .ant-upload {
          width: 100% !important;
        }
        .animate-spin {
          animation: spin 1s linear infinite;
        }
        @keyframes spin {
          from { transform: rotate(0deg); }
          to { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
};