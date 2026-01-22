"use client";

import { Card, Collapse } from "antd";
import { MailOutlined, PhoneOutlined } from "@ant-design/icons";

const { Panel } = Collapse;

export default function HelpSection() {
  return (
    <Card title="Help & Support" className="mt-6 shadow-sm">
      
      {/* FAQ Section */}
      <Collapse accordion>
        <Panel header="How do I submit feedback?" key="1">
          Go to the Feedback form, select type & module, enter message and submit.
        </Panel>

        <Panel header="Who can see my feedback?" key="2">
          Only the internal admin and support team can view your feedback.
        </Panel>

        <Panel header="What if I face login issues?" key="3">
          Please contact support using the details below.
        </Panel>
        
         <Panel header="How do I assign a driver to a vehicle?" key="4">
          Go to Dispatch or Consignment module and select an available driver while creating or updating the record.
        </Panel>
      </Collapse>

      {/* Contact Section */}
      <div className="mt-6 space-y-2">
        <p>
          <MailOutlined /> <strong>Email:</strong> nazim@tms.com
        </p>
        <p>
          <PhoneOutlined /> <strong>Phone:</strong> +91 98765 43210
        </p>
      </div>

    </Card>
  );
}
