import React, { useState } from 'react';
import { 
  Upload, 
  Button, 
  message, 
  Card, 
  Typography, 
  Space, 
  List, 
  Progress,
  Popconfirm,
  Tag,
  Row,
  Col
} from 'antd';
import { 
  UploadOutlined, 
  FileTextOutlined, 
  DeleteOutlined, 
  EyeOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons';

const { Title, Text } = Typography;
const { Dragger } = Upload;

const CustomsFileUpload = () => {
  const [fileList, setFileList] = useState([]);
  const [uploading, setUploading] = useState(false);

  const acceptedFormats = [
    'CSV', 'XLS', 'XLSX', 
  ];

const customsDocumentTypes = [
  "vehicle_types.xlsx",
  "expense_types.xlsx",
  "product_categories.xlsx",
  "locations.xlsx",
  "branches.xlsx",

];


  const beforeUpload = (file) => {
    const isValidFormat = acceptedFormats.some(format => 
      file.name.toLowerCase().endsWith(format.toLowerCase())
    );
    
    if (!isValidFormat) {
      message.error(`${file.name} is not a supported file format`);
      return false;
    }
    
    const isLt10M = file.size / 1024 / 1024 < 10;
    if (!isLt10M) {
      message.error('File must be smaller than 10MB');
      return false;
    }
    
    return false; // Prevent automatic upload
  };

  const handleChange = ({ fileList: newFileList }) => {
    setFileList(newFileList);
  };

  const handleUpload = () => {
    setUploading(true);
    
    // Simulate upload process
    setTimeout(() => {
      setFileList(prev => prev.map(file => ({
        ...file,
        status: 'done',
        percent: 100
      })));
      setUploading(false);
      message.success('All files uploaded successfully');
    }, 2000);
  };

  const handleRemove = (file) => {
    setFileList(prev => prev.filter(item => item.uid !== file.uid));
    message.success('File removed successfully');
  };

  const handlePreview = async (file) => {
    if (file.url || file.preview) {
      const url = file.url || file.preview;
      const link = document.createElement('a');
      link.href = url;
      link.target = '_blank';
      link.click();
    }
  };

  const getFileIcon = (fileName) => {
    const extension = fileName.split('.').pop().toLowerCase();
    switch (extension) {
      case 'csv':
        return <FileTextOutlined style={{ color: '#f39c12' }} />;
      case 'xls':
      case 'xlsx':
        return <FileTextOutlined style={{ color: '#4caf50' }} />;

      default:
        return <FileTextOutlined style={{ color: '#666' }} />;
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'done':
        return <CheckCircleOutlined style={{ color: '#52c41a' }} />;
      case 'error':
        return <ExclamationCircleOutlined style={{ color: '#ff4d4f' }} />;
      default:
        return null;
    }
  };

  const uploadProps = {
    name: 'file',
    multiple: true,
    fileList,
    beforeUpload,
    onChange: handleChange,
    onRemove: false, // Disable default remove
    showUploadList: false, // We'll create custom list
  };

  return (
    <div style={{ padding: '24px', maxWidth: '1200px', margin: '0 auto' }}>
      <Card 
        title={
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <UploadOutlined style={{ color: '#a61e1e' }} />
            <Title level={3} style={{ margin: 0, color: '#a61e1e' }}>
              Customs Document Upload
            </Title>
          </div>
        }
        style={{ 
          borderRadius: '8px',
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
        }}
      >
        <Row gutter={[24, 24]}>
          <Col xs={24} lg={14}>
            <Dragger 
              {...uploadProps}
              style={{ 
                border: '2px dashed #a61e1e',
                borderRadius: '8px',
                backgroundColor: '#fafafa'
              }}
            >
              <p className="ant-upload-drag-icon">
                <UploadOutlined style={{ fontSize: '48px', color: '#a61e1e' }} />
              </p>
              <p className="ant-upload-text" style={{ fontSize: '16px', color: '#333' }}>
                Click or drag customs documents to this area to upload
              </p>
              <p className="ant-upload-hint" style={{ color: '#666' }}>
                Support for single or bulk upload. Accepted formats: {acceptedFormats.join(', ')}
                <br />
                Maximum file size: 10MB per file
              </p>
            </Dragger>

            <div style={{ marginTop: '16px', textAlign: 'center' }}>
              <Button 
                type="primary" 
                onClick={handleUpload}
                disabled={fileList.length === 0}
                loading={uploading}
                style={{ 
                  backgroundColor: '#a61e1e',
                  borderColor: '#a61e1e',
                  height: '40px',
                  paddingLeft: '24px',
                  paddingRight: '24px'
                }}
              >
                {uploading ? 'Uploading...' : `Upload ${fileList.length} File(s)`}
              </Button>
            </div>
          </Col>

          <Col xs={24} lg={10}>
            <Card 
              size="small" 
              title="Document Types"
              style={{ 
                backgroundColor: '#f8f9fa',
                border: '1px solid #e9ecef'
              }}
            >
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
                {customsDocumentTypes.map(type => (
                  <Tag 
                    key={type} 
                    color="blue"
                    style={{ marginBottom: '4px' }}
                  >
                    {type}
                  </Tag>
                ))}
              </div>
            </Card>
          </Col>
        </Row>

        {fileList.length > 0 && (
          <div style={{ marginTop: '24px' }}>
            <Title level={4} style={{ color: '#a61e1e', marginBottom: '16px' }}>
              Uploaded Files ({fileList.length})
            </Title>
            
            <List
              dataSource={fileList}
              renderItem={(file) => (
                <List.Item
                  style={{
                    padding: '12px 16px',
                    border: '1px solid #e9ecef',
                    borderRadius: '6px',
                    marginBottom: '8px',
                    backgroundColor: '#fff'
                  }}
                  actions={[
                    <Button
                      type="text"
                      icon={<EyeOutlined />}
                      onClick={() => handlePreview(file)}
                      style={{ color: '#a61e1e' }}
                    >
                      Preview
                    </Button>,
                    <Popconfirm
                      title="Are you sure you want to remove this file?"
                      onConfirm={() => handleRemove(file)}
                      okText="Yes"
                      cancelText="No"
                      okButtonProps={{ style: { backgroundColor: '#a61e1e', borderColor: '#a61e1e' } }}
                    >
                      <Button
                        type="text"
                        danger
                        icon={<DeleteOutlined />}
                      >
                        Remove
                      </Button>
                    </Popconfirm>
                  ]}
                >
                  <List.Item.Meta
                    avatar={getFileIcon(file.name)}
                    title={
                      <Space>
                        <Text strong>{file.name}</Text>
                        {getStatusIcon(file.status)}
                      </Space>
                    }
                    description={
                      <Space direction="vertical" size="small" style={{ width: '100%' }}>
                        <Text type="secondary">
                          Size: {(file.size / 1024 / 1024).toFixed(2)} MB
                        </Text>
                        {file.status === 'uploading' && (
                          <Progress 
                            percent={file.percent || 0} 
                            size="small"
                            strokeColor="#a61e1e"
                          />
                        )}
                      </Space>
                    }
                  />
                </List.Item>
              )}
            />
          </div>
        )}
      </Card>
    </div>
  );
};

export default CustomsFileUpload;