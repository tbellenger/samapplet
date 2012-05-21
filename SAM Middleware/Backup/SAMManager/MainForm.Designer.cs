namespace SAMManager
{
    partial class MainForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
            this.btn_Connect = new System.Windows.Forms.Button();
            this.btn_Load = new System.Windows.Forms.Button();
            this.mskTxtPIN = new System.Windows.Forms.MaskedTextBox();
            this.btn_Verify = new System.Windows.Forms.Button();
            this.mskTxtUpdate = new System.Windows.Forms.MaskedTextBox();
            this.btn_Update = new System.Windows.Forms.Button();
            this.mskTxtPass = new System.Windows.Forms.MaskedTextBox();
            this.btn_Store = new System.Windows.Forms.Button();
            this.txtSlot = new System.Windows.Forms.TextBox();
            this.chkOverwrite = new System.Windows.Forms.CheckBox();
            this.txtRetrPass = new System.Windows.Forms.TextBox();
            this.txtSlotRetr = new System.Windows.Forms.TextBox();
            this.btn_RetrPass = new System.Windows.Forms.Button();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            this.groupBox3 = new System.Windows.Forms.GroupBox();
            this.groupBox4 = new System.Windows.Forms.GroupBox();
            this.btnLockLoad = new System.Windows.Forms.Button();
            this.txtCompKCV = new System.Windows.Forms.TextBox();
            this.txtZMKSlot = new System.Windows.Forms.TextBox();
            this.txtCompIndex = new System.Windows.Forms.TextBox();
            this.btn_ComponentLoad = new System.Windows.Forms.Button();
            this.mskTxtZMKComp = new System.Windows.Forms.MaskedTextBox();
            this.groupBox5 = new System.Windows.Forms.GroupBox();
            this.txtKeyKCV = new System.Windows.Forms.TextBox();
            this.txtZMKSlot2 = new System.Windows.Forms.TextBox();
            this.txtKeySlot = new System.Windows.Forms.TextBox();
            this.btn_LoadCryptogram = new System.Windows.Forms.Button();
            this.txtCryptogram = new System.Windows.Forms.TextBox();
            this.groupBox1.SuspendLayout();
            this.groupBox2.SuspendLayout();
            this.groupBox3.SuspendLayout();
            this.groupBox4.SuspendLayout();
            this.groupBox5.SuspendLayout();
            this.SuspendLayout();
            // 
            // btn_Connect
            // 
            this.btn_Connect.Location = new System.Drawing.Point(8, 19);
            this.btn_Connect.Name = "btn_Connect";
            this.btn_Connect.Size = new System.Drawing.Size(71, 21);
            this.btn_Connect.TabIndex = 1;
            this.btn_Connect.Text = "Connect";
            this.btn_Connect.UseVisualStyleBackColor = true;
            // 
            // btn_Load
            // 
            this.btn_Load.Location = new System.Drawing.Point(85, 19);
            this.btn_Load.Name = "btn_Load";
            this.btn_Load.Size = new System.Drawing.Size(71, 21);
            this.btn_Load.TabIndex = 2;
            this.btn_Load.Text = "Load App";
            this.btn_Load.UseVisualStyleBackColor = true;
            this.btn_Load.Click += new System.EventHandler(this.btn_Load_Click);
            // 
            // mskTxtPIN
            // 
            this.mskTxtPIN.Location = new System.Drawing.Point(6, 19);
            this.mskTxtPIN.Name = "mskTxtPIN";
            this.mskTxtPIN.Size = new System.Drawing.Size(73, 20);
            this.mskTxtPIN.TabIndex = 3;
            this.mskTxtPIN.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.mskTxtPIN.UseSystemPasswordChar = true;
            // 
            // btn_Verify
            // 
            this.btn_Verify.Location = new System.Drawing.Point(85, 18);
            this.btn_Verify.Name = "btn_Verify";
            this.btn_Verify.Size = new System.Drawing.Size(71, 21);
            this.btn_Verify.TabIndex = 4;
            this.btn_Verify.Text = "Verify PIN";
            this.btn_Verify.UseVisualStyleBackColor = true;
            this.btn_Verify.Click += new System.EventHandler(this.btn_Verify_Click);
            // 
            // mskTxtUpdate
            // 
            this.mskTxtUpdate.Location = new System.Drawing.Point(162, 19);
            this.mskTxtUpdate.Name = "mskTxtUpdate";
            this.mskTxtUpdate.Size = new System.Drawing.Size(73, 20);
            this.mskTxtUpdate.TabIndex = 5;
            this.mskTxtUpdate.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.mskTxtUpdate.UseSystemPasswordChar = true;
            // 
            // btn_Update
            // 
            this.btn_Update.Location = new System.Drawing.Point(241, 18);
            this.btn_Update.Name = "btn_Update";
            this.btn_Update.Size = new System.Drawing.Size(71, 21);
            this.btn_Update.TabIndex = 6;
            this.btn_Update.Text = "Update PIN";
            this.btn_Update.UseVisualStyleBackColor = true;
            this.btn_Update.Click += new System.EventHandler(this.btn_Update_Click);
            // 
            // mskTxtPass
            // 
            this.mskTxtPass.Location = new System.Drawing.Point(6, 19);
            this.mskTxtPass.Name = "mskTxtPass";
            this.mskTxtPass.Size = new System.Drawing.Size(229, 20);
            this.mskTxtPass.TabIndex = 7;
            this.mskTxtPass.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.mskTxtPass.UseSystemPasswordChar = true;
            // 
            // btn_Store
            // 
            this.btn_Store.Location = new System.Drawing.Point(241, 18);
            this.btn_Store.Name = "btn_Store";
            this.btn_Store.Size = new System.Drawing.Size(71, 21);
            this.btn_Store.TabIndex = 8;
            this.btn_Store.Text = "Store Pass";
            this.btn_Store.UseVisualStyleBackColor = true;
            this.btn_Store.Click += new System.EventHandler(this.btn_Store_Click);
            // 
            // txtSlot
            // 
            this.txtSlot.Location = new System.Drawing.Point(318, 19);
            this.txtSlot.Name = "txtSlot";
            this.txtSlot.Size = new System.Drawing.Size(19, 20);
            this.txtSlot.TabIndex = 9;
            this.txtSlot.Text = "0";
            this.txtSlot.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            // 
            // chkOverwrite
            // 
            this.chkOverwrite.AutoSize = true;
            this.chkOverwrite.Location = new System.Drawing.Point(343, 21);
            this.chkOverwrite.Name = "chkOverwrite";
            this.chkOverwrite.Size = new System.Drawing.Size(71, 17);
            this.chkOverwrite.TabIndex = 10;
            this.chkOverwrite.Text = "Overwrite";
            this.chkOverwrite.UseVisualStyleBackColor = true;
            // 
            // txtRetrPass
            // 
            this.txtRetrPass.Enabled = false;
            this.txtRetrPass.Location = new System.Drawing.Point(6, 45);
            this.txtRetrPass.Name = "txtRetrPass";
            this.txtRetrPass.Size = new System.Drawing.Size(229, 20);
            this.txtRetrPass.TabIndex = 11;
            // 
            // txtSlotRetr
            // 
            this.txtSlotRetr.Location = new System.Drawing.Point(318, 45);
            this.txtSlotRetr.Name = "txtSlotRetr";
            this.txtSlotRetr.Size = new System.Drawing.Size(19, 20);
            this.txtSlotRetr.TabIndex = 12;
            this.txtSlotRetr.Text = "0";
            this.txtSlotRetr.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            // 
            // btn_RetrPass
            // 
            this.btn_RetrPass.Location = new System.Drawing.Point(241, 45);
            this.btn_RetrPass.Name = "btn_RetrPass";
            this.btn_RetrPass.Size = new System.Drawing.Size(71, 21);
            this.btn_RetrPass.TabIndex = 13;
            this.btn_RetrPass.Text = "Get Pass";
            this.btn_RetrPass.UseVisualStyleBackColor = true;
            this.btn_RetrPass.Click += new System.EventHandler(this.btn_RetrPass_Click);
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.btn_Connect);
            this.groupBox1.Controls.Add(this.btn_Load);
            this.groupBox1.Location = new System.Drawing.Point(4, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(456, 54);
            this.groupBox1.TabIndex = 14;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Reader Connection";
            // 
            // groupBox2
            // 
            this.groupBox2.Controls.Add(this.mskTxtPIN);
            this.groupBox2.Controls.Add(this.btn_Verify);
            this.groupBox2.Controls.Add(this.mskTxtUpdate);
            this.groupBox2.Controls.Add(this.btn_Update);
            this.groupBox2.Location = new System.Drawing.Point(4, 72);
            this.groupBox2.Name = "groupBox2";
            this.groupBox2.Size = new System.Drawing.Size(456, 53);
            this.groupBox2.TabIndex = 15;
            this.groupBox2.TabStop = false;
            this.groupBox2.Text = "PIN Management";
            // 
            // groupBox3
            // 
            this.groupBox3.Controls.Add(this.mskTxtPass);
            this.groupBox3.Controls.Add(this.btn_Store);
            this.groupBox3.Controls.Add(this.txtSlot);
            this.groupBox3.Controls.Add(this.btn_RetrPass);
            this.groupBox3.Controls.Add(this.chkOverwrite);
            this.groupBox3.Controls.Add(this.txtSlotRetr);
            this.groupBox3.Controls.Add(this.txtRetrPass);
            this.groupBox3.Location = new System.Drawing.Point(4, 131);
            this.groupBox3.Name = "groupBox3";
            this.groupBox3.Size = new System.Drawing.Size(456, 80);
            this.groupBox3.TabIndex = 16;
            this.groupBox3.TabStop = false;
            this.groupBox3.Text = "Password Store";
            // 
            // groupBox4
            // 
            this.groupBox4.Controls.Add(this.btnLockLoad);
            this.groupBox4.Controls.Add(this.txtCompKCV);
            this.groupBox4.Controls.Add(this.txtZMKSlot);
            this.groupBox4.Controls.Add(this.txtCompIndex);
            this.groupBox4.Controls.Add(this.btn_ComponentLoad);
            this.groupBox4.Controls.Add(this.mskTxtZMKComp);
            this.groupBox4.Location = new System.Drawing.Point(4, 217);
            this.groupBox4.Name = "groupBox4";
            this.groupBox4.Size = new System.Drawing.Size(456, 74);
            this.groupBox4.TabIndex = 17;
            this.groupBox4.TabStop = false;
            this.groupBox4.Text = "ZMK Management";
            // 
            // btnLockLoad
            // 
            this.btnLockLoad.Location = new System.Drawing.Point(241, 45);
            this.btnLockLoad.Name = "btnLockLoad";
            this.btnLockLoad.Size = new System.Drawing.Size(71, 21);
            this.btnLockLoad.TabIndex = 13;
            this.btnLockLoad.Text = "Lock Load";
            this.btnLockLoad.UseVisualStyleBackColor = true;
            this.btnLockLoad.Click += new System.EventHandler(this.btnLockLoad_Click);
            // 
            // txtCompKCV
            // 
            this.txtCompKCV.Location = new System.Drawing.Point(368, 19);
            this.txtCompKCV.Name = "txtCompKCV";
            this.txtCompKCV.Size = new System.Drawing.Size(46, 20);
            this.txtCompKCV.TabIndex = 12;
            this.txtCompKCV.Text = "KCV";
            this.txtCompKCV.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            // 
            // txtZMKSlot
            // 
            this.txtZMKSlot.Location = new System.Drawing.Point(343, 19);
            this.txtZMKSlot.Name = "txtZMKSlot";
            this.txtZMKSlot.Size = new System.Drawing.Size(19, 20);
            this.txtZMKSlot.TabIndex = 11;
            this.txtZMKSlot.Text = "0";
            this.txtZMKSlot.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            // 
            // txtCompIndex
            // 
            this.txtCompIndex.Location = new System.Drawing.Point(318, 19);
            this.txtCompIndex.Name = "txtCompIndex";
            this.txtCompIndex.Size = new System.Drawing.Size(19, 20);
            this.txtCompIndex.TabIndex = 10;
            this.txtCompIndex.Text = "0";
            this.txtCompIndex.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            // 
            // btn_ComponentLoad
            // 
            this.btn_ComponentLoad.Location = new System.Drawing.Point(241, 18);
            this.btn_ComponentLoad.Name = "btn_ComponentLoad";
            this.btn_ComponentLoad.Size = new System.Drawing.Size(71, 21);
            this.btn_ComponentLoad.TabIndex = 9;
            this.btn_ComponentLoad.Text = "Comp Load";
            this.btn_ComponentLoad.UseVisualStyleBackColor = true;
            this.btn_ComponentLoad.Click += new System.EventHandler(this.btn_ComponentLoad_Click);
            // 
            // mskTxtZMKComp
            // 
            this.mskTxtZMKComp.Location = new System.Drawing.Point(6, 19);
            this.mskTxtZMKComp.Name = "mskTxtZMKComp";
            this.mskTxtZMKComp.Size = new System.Drawing.Size(229, 20);
            this.mskTxtZMKComp.TabIndex = 8;
            this.mskTxtZMKComp.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.mskTxtZMKComp.UseSystemPasswordChar = true;
            // 
            // groupBox5
            // 
            this.groupBox5.Controls.Add(this.txtKeyKCV);
            this.groupBox5.Controls.Add(this.txtZMKSlot2);
            this.groupBox5.Controls.Add(this.txtKeySlot);
            this.groupBox5.Controls.Add(this.btn_LoadCryptogram);
            this.groupBox5.Controls.Add(this.txtCryptogram);
            this.groupBox5.Location = new System.Drawing.Point(4, 297);
            this.groupBox5.Name = "groupBox5";
            this.groupBox5.Size = new System.Drawing.Size(456, 54);
            this.groupBox5.TabIndex = 18;
            this.groupBox5.TabStop = false;
            this.groupBox5.Text = "Key Management";
            // 
            // txtKeyKCV
            // 
            this.txtKeyKCV.Location = new System.Drawing.Point(368, 19);
            this.txtKeyKCV.Name = "txtKeyKCV";
            this.txtKeyKCV.Size = new System.Drawing.Size(46, 20);
            this.txtKeyKCV.TabIndex = 17;
            this.txtKeyKCV.Text = "KCV";
            this.txtKeyKCV.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            // 
            // txtZMKSlot2
            // 
            this.txtZMKSlot2.Location = new System.Drawing.Point(343, 19);
            this.txtZMKSlot2.Name = "txtZMKSlot2";
            this.txtZMKSlot2.Size = new System.Drawing.Size(19, 20);
            this.txtZMKSlot2.TabIndex = 16;
            this.txtZMKSlot2.Text = "0";
            this.txtZMKSlot2.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            // 
            // txtKeySlot
            // 
            this.txtKeySlot.Location = new System.Drawing.Point(318, 19);
            this.txtKeySlot.Name = "txtKeySlot";
            this.txtKeySlot.Size = new System.Drawing.Size(19, 20);
            this.txtKeySlot.TabIndex = 15;
            this.txtKeySlot.Text = "0";
            this.txtKeySlot.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            // 
            // btn_LoadCryptogram
            // 
            this.btn_LoadCryptogram.Location = new System.Drawing.Point(241, 18);
            this.btn_LoadCryptogram.Name = "btn_LoadCryptogram";
            this.btn_LoadCryptogram.Size = new System.Drawing.Size(71, 21);
            this.btn_LoadCryptogram.TabIndex = 14;
            this.btn_LoadCryptogram.Text = "Cryptogram";
            this.btn_LoadCryptogram.UseVisualStyleBackColor = true;
            this.btn_LoadCryptogram.Click += new System.EventHandler(this.btn_LoadCryptogram_Click);
            // 
            // txtCryptogram
            // 
            this.txtCryptogram.Location = new System.Drawing.Point(6, 19);
            this.txtCryptogram.Name = "txtCryptogram";
            this.txtCryptogram.Size = new System.Drawing.Size(229, 20);
            this.txtCryptogram.TabIndex = 13;
            this.txtCryptogram.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(464, 357);
            this.Controls.Add(this.groupBox5);
            this.Controls.Add(this.groupBox4);
            this.Controls.Add(this.groupBox3);
            this.Controls.Add(this.groupBox2);
            this.Controls.Add(this.groupBox1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "MainForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "SAM Manager";
            this.Load += new System.EventHandler(this.MainForm_Load);
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.MainForm_FormClosing);
            this.groupBox1.ResumeLayout(false);
            this.groupBox2.ResumeLayout(false);
            this.groupBox2.PerformLayout();
            this.groupBox3.ResumeLayout(false);
            this.groupBox3.PerformLayout();
            this.groupBox4.ResumeLayout(false);
            this.groupBox4.PerformLayout();
            this.groupBox5.ResumeLayout(false);
            this.groupBox5.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button btn_Connect;
        private System.Windows.Forms.Button btn_Load;
        private System.Windows.Forms.MaskedTextBox mskTxtPIN;
        private System.Windows.Forms.Button btn_Verify;
        private System.Windows.Forms.MaskedTextBox mskTxtUpdate;
        private System.Windows.Forms.Button btn_Update;
        private System.Windows.Forms.MaskedTextBox mskTxtPass;
        private System.Windows.Forms.Button btn_Store;
        private System.Windows.Forms.TextBox txtSlot;
        private System.Windows.Forms.CheckBox chkOverwrite;
        private System.Windows.Forms.TextBox txtRetrPass;
        private System.Windows.Forms.TextBox txtSlotRetr;
        private System.Windows.Forms.Button btn_RetrPass;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.GroupBox groupBox2;
        private System.Windows.Forms.GroupBox groupBox3;
        private System.Windows.Forms.GroupBox groupBox4;
        private System.Windows.Forms.TextBox txtZMKSlot;
        private System.Windows.Forms.TextBox txtCompIndex;
        private System.Windows.Forms.Button btn_ComponentLoad;
        private System.Windows.Forms.MaskedTextBox mskTxtZMKComp;
        private System.Windows.Forms.TextBox txtCompKCV;
        private System.Windows.Forms.Button btnLockLoad;
        private System.Windows.Forms.GroupBox groupBox5;
        private System.Windows.Forms.TextBox txtCryptogram;
        private System.Windows.Forms.TextBox txtKeyKCV;
        private System.Windows.Forms.TextBox txtZMKSlot2;
        private System.Windows.Forms.TextBox txtKeySlot;
        private System.Windows.Forms.Button btn_LoadCryptogram;
    }
}

