"use client";

import { useState } from "react";
import { Card, Form, Input, Select, Button, Rate, message } from "antd";
import Title from "@/Components/atom/Title";
import { submitFeedback } from "@/service/feedback";
import HelpSection from "@/Components/Help/HelpSection";

export default function HelpFeedbackPage() {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (values) => {
    console.log("Submitting feedback with values:", values); // ✅ will print

    try {
      setLoading(true);

      const payload = {
        feedbackType: values.type,
        feedbackModule: values.module,
        feedBackMessage: values.message,
        feedbackRating: values.rating || 0,
      };

      console.log("Payload to be sent:", payload); // ✅ will print
      await submitFeedback(payload);

      message.success("Feedback submitted successfully 🙏");
      form.resetFields();
    } catch (error) {
      console.error(error);
      message.error("Failed to submit feedback. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 max-w-3xl mx-auto">
      <Title title="Help & Feedback" />

      <Card className="mt-4 shadow-sm">
        <Form
          layout="vertical"
          form={form}
          onFinish={handleSubmit}   // ✅ correct
        >
          <Form.Item label="Feedback Type" name="type" rules={[{ required: true }]}>
            <Select placeholder="Select type">
              <Select.Option value="BUG">Bug Report</Select.Option>
              <Select.Option value="FEATURE">Feature Request</Select.Option>
              <Select.Option value="UI">UI / UX Issue</Select.Option>
              <Select.Option value="PERFORMANCE">Performance Issue</Select.Option>
              <Select.Option value="GENERAL">General Feedback</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item label="Module" name="module" rules={[{ required: true }]}>
            <Select placeholder="Select module">
              <Select.Option value="DRIVER">Driver</Select.Option>
              <Select.Option value="VEHICLE">Vehicle</Select.Option>
              <Select.Option value="CONSIGNMENT">Consignment</Select.Option>
              <Select.Option value="DISPATCH">Dispatch</Select.Option>
              <Select.Option value="OTHER">Other</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="Message"
            name="message"
            rules={[
              { required: true },
              { min: 10, message: "Message must be at least 10 characters" },
            ]}
          >
            <Input.TextArea rows={4} />
          </Form.Item>

          <Form.Item label="Rating" name="rating">
            <Rate />
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              danger
              htmlType="submit"
              loading={loading}
            >
              Submit Feedback
            </Button>
          </Form.Item>
        </Form>
      </Card>
      {<HelpSection />}
    </div>
  );
}
